import de.sciss.synth._
import de.sciss.synth.Env.Seg;
import de.sciss.synth.Env.Seg._;
import ugen._
import org.mt4j.output.audio.{Changed1, AudioServer}

SynthDef("Wobble2") {

	// PARAMETERS  -----
	val gate = "gate".kr(1)
	val volume = "volume".kr(0.083)
	val rotationZ = "rotationZ".kr(0)
	val rotationY = "rotationY".kr(0)
	val rotationX = "rotationX".kr(0)
	val beatDuration = "beatDuration".kr(1000)


	// FUNCTIONS -----
	def range(signal: GE, low: GE, high: GE): GE = {
		((high + low) / 2) + (signal * (high - low) / 2)
	}
	/**
	 * rotation to sound-frequence, discretised to the 12 halftones
	 * @param rotation in degrees
	 * @return GE sound-frequence in Hz
	 */
	def halftone(rotation: GE): GE = {
		55 * 2.pow((rotation.abs / 30).floor / 12)
	}
	/**
	 * rotation to duration of the sound
	 * @param rotation in degrees
	 * @return GE duration in seconds
	 */
	def duration(rotation: GE): GE = {
		(((rotation.abs) / 360).floor + 1) * (beatDuration / 1000)
	}
	/**
	 * rotation to wobbles per seconds
	 * 0 - 30  degrees -> 1
	 * 30 - 60  degrees -> 2
	 * 60 - 90  degrees -> 3 ...
	 * @param rotation in degrees
	 * @return GE wobbles per seconds
	 */
	def wobbles(rotation: GE): GE = {
		(rotation.abs / 30).floor + 1
	}
	
	def scaledRot(rotation:GE): GE = {
		Clip.kr(rotation / 720, 0, 1);
	}
	
	


	// CREATING SOUND -----

	//var envc1, envc2, outc, noisec1, noisec2, envs0, envs1, envs1m, envs2: GE;
	//var oscs, noises, outs:GE;
	//var mix, freq:GE;
	 
	val freq = -scaledRot(rotationZ)*20;
	val trig = Changed1.kr(gate);
	 
	// clap envelope
	val envc2 = EnvGen.ar(Env(0, Vector[Seg](Seg(0.02,1,curveShape(0)), Seg(0.3,0, curveShape(-4)))), trig, doneAction=0);
	val envc1 = EnvGen.ar(Env(
	0,
	Vector[Env.Seg](
		Seg(0.001, 1, curveShape(0)),
		Seg(0.013, 0, curveShape(-3)),
		Seg(0.0001, 1,curveShape(0)),		
		Seg(0.01, 0,  curveShape(-3)),
		Seg(0.0001, 1,curveShape(0)),
		Seg(0.01, 0,  curveShape(-3)),
		Seg(0.0001, 1,curveShape(0)),		
		Seg(0.03, 0,  curveShape(-4))
	)), trig, doneAction=0);
	 
	// snare envelope
	var envs0 = EnvGen.ar(Env(
	0.5, Vector[Env.Seg](Seg(0.005,1, curveShape(-4)), Seg(0.03,0.5, curveShape(-2)), Seg(0.1, 0, curveShape(-4))))
	, trig);
	var envs1 = EnvGen.ar(Env(
	110, Vector[Env.Seg](Seg(0.005,60, curveShape(-4)), Seg(0.1,49, curveShape(-5))))
	, trig);
	
	var envBase = EnvGen.kr(Env.perc(0.001, 0.01), trig)
	var envs1m = (freq+envs1).midicps;
	var envs2 = EnvGen.ar(Env( 1, Vector[Env.Seg](Seg(0.05,0.4, curveShape(-2)), Seg(0.13,0, curveShape(-2)))) , trig);
	 
	 
	// clap signal
	var noisec1:GE = WhiteNoise.ar(envc1);
	noisec1 = HPF.ar(noisec1, 600);
	noisec1 = BPF.ar(noisec1, 2000, 3);
	 
	var noisec2:GE = WhiteNoise.ar(envc2);
	noisec2 = HPF.ar(noisec2, 1000);
	noisec2 = (BPF.ar(noisec2, 1200)* 0.7) + 0.7;
	 
	var outc = noisec1 + noisec2;
	outc = outc * 2;
	outc = outc.softclip * volume;
	 
	// snare signal
	 
	var oscs = de.sciss.synth.ugen.LFPulse.ar(envs1m, 0, 0.5) - 0.5  + (de.sciss.synth.ugen.LFPulse.ar(envs1m * 1.6, 0, 0.5) * 0.5 - 0.25);
	oscs = LPF.ar(oscs, envs1m*1.2)* envs0;
	oscs = oscs + ( SinOsc.ar(envs1m, 0.8) * envs0 );
	 
	var noises:GE = WhiteNoise.ar(0.2);
	noises = HPF.ar(noises, 200)* 2;
	noises = ( BPF.ar(noises, 6900) * 0.6 + 3) + noises;
	noises = noises * envs2 * (1-scaledRot(rotationX));
	 
	var outs = oscs + noises ;
	outs = outs.clip2(1) + envs2*(scaledRot(rotationZ)*SinOsc.ar(envBase*1000+80)+0.5*SinOsc.ar(envBase*10000+110)) * volume;
	 
	var mix = LinXFade2.ar(outc, outs, scaledRot(rotationY) - 1);

	val sig = Pan2.ar(SplayAz.ar(2, mix));



 

	AudioServer.attach(sig)

}