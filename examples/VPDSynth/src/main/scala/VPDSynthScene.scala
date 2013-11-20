/*
++1>>  This source code is licensed as GPLv3 if not stated otherwise.
>>  NO responsibility taken for ANY harm, damage done
>>  to you, your data, animals, etc.
>>
+2>>
>>
+3>>
>>  Copyright (c) 2012:
>>
>>     |             |     |
>>     |    ,---.,---|,---.|---.
>>     |    |   ||   |`---.|   |
>>     `---'`---'`---'`---'`---'
>>                    // Niklas Klügel
>>
+4>>
>>  Made in Bavaria by fat little elves - since 1983.
*/

import org.mt4j.components.MTComponent
import org.mt4j.components.visibleComponents.widgets.{MTSlider, MTBackgroundImage, Button, Slider, TextArea}
import org.mt4j.input.midi.{MidiCtrlMsg, MidiNoteOffMsg, MidiNoteOnMsg, MidiCommunication}
import org.mt4j.util.math.Vector3D
import org.mt4j.{Scene, Application}
import org.mt4j.types.{Vec3d}
import org.mt4j.components.ComponentImplicits._
import org.lodsb.reakt.Implicits._
import org.mt4j.components.visibleComponents.font.FontManager
import org.mt4j.components.visibleComponents.shapes.{MTRectangle, MTRoundRectangle}
import org.mt4j.components.TransformSpace
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor

import org.mt4j.util.Color
import org.mt4j.util.MTColor
import org.mt4j.util.Color._

import org.mt4j.output.audio.AudioServer._


import de.sciss.synth._
import de.sciss.synth.ugen.{MouseY, SinOsc, MouseX, Impulse}
import org.mt4j.output.audio.{Synthesizer, AudioServer}
import de.sciss.synth._
import de.sciss.synth.ugen._
import de.sciss.synth.Ops._
import org.lodsb.VPDSynth._


object VPDSynthApp extends Application {

  def main(args: Array[String]) {


    this.execute(false)

  }


  override def startUp() = {
    this.addScene(new VPDSynthScene(this, "VPDSynthApp"))
  }


}


object VPDSynthScene {
  
  final val SliderLength = VPDSynthApp.height/7f
  final val SliderWidth = SliderLength/5f
  
}


class VPDSynthScene(app: Application, name: String) extends Scene(app, name) {


  AudioServer.start(true)

  def buildSynth(cfmp: Float, mfmp: Float, freqp: Float,
                 aetp: Float, cvpyp: Float, mvpyp: Float, cvpxp: Float,
                 mvpxp: Float, cvpywp: Float, mvpywp: Float, cvpxwp: Float, mvpxwp: Float, fmtp: Float,
                 fmidxp: Float, nap: Float, fxrtp: Float): SynthDef = SynthDef("VPDTestSynthGated") {

    val clag = "cLag".kr(0.01)
    val gr = "gate".kr
    val cfm = Lag.kr("cleanFmRingmod".kr, clag)
    val mfm = Lag.kr("modFreqMult".kr, clag)
    val freq = "frequency".kr
    val aEt = Lag.kr("ampEnvType".kr, clag)
    val cvpY = Lag.kr("carrierVPSYType".kr, clag)
    val mvpY = Lag.kr("modulatorVPSYType".kr, clag)
    val cvpX = Lag.kr("carrierVPSXType".kr, clag)
    val mvpX = Lag.kr("modulatorVPSXType".kr, clag)
    val cvpYW = Lag.kr("carrierVPSYWeight".kr, clag)
    val mvpYW = Lag.kr("modulatorVPSYWeight".kr, clag)
    val cvpXW = Lag.kr("carrierVPSXWeight".kr, clag)
    val mvpXW = Lag.kr("modulatorVPSXWeight".kr, clag)
    val fmT = Lag.kr("fmModType".kr, clag)
    val fmIdx = Lag.kr("fmModIdx".kr, clag)
    val nA = Lag.kr("noiseAmount".kr, clag)
    val fxRT = Lag.kr("fxRouteType".kr, clag)
    val vol = "volume".kr(0.8)

    val out = VPDSynthGated.ar(gr,
      cfm, mfm, freq, aEt, cvpY, mvpY, cvpX, mvpX, cvpYW, mvpYW, cvpXW, mvpXW, fmT, fmIdx, nA, fxRT, vol
    )

    val mix = Mix(out)


    //AudioServer attach out
    /*Out.ar(0, out)
    Out.ar(1, out)
    Out.ar(2, out)
    Out.ar(3, out)*/

    Out.ar(2, mix)
    Out.ar(3, mix)
    Out.ar(4, mix)
    Out.ar(5, mix)
  }


  showTracer(true)

  private val parameterMapping = Seq[(String, Int, (Float, Float))](
   // ("frequency", 6, (2.0f, 1000.0f)), //dont show the frequency
    ("ampEnvType", 6, (0.0f, 1.0f)),
    ("cleanFmRingmod", 7, (-0.25f, 1.0f)),
    ("fmModIdx", 7, (0.0f, 1000.0f)),
    ("fmModType", 7, (0.0f, 1.0f)),
    ("modFreqMult", 7, (0.0f, 1.0f)),
    ("carrierVPSXType", 1, (0.0f, 1.0f)),
    ("modulatorVPSXType", 2, (0.0f, 1.0f)),
    ("carrierVPSYType", 3, (0.0f, 1.0f)),
    ("modulatorVPSYType", 4, (0.0f, 1.0f)),
    ("carrierVPSXWeight", 1, (0.0f, 20.0f)),
    ("modulatorVPSXWeight", 2, (0.0f, 20.0f)),
    ("carrierVPSYWeight", 3, (0.0f, 20.0f)),
    ("modulatorVPSYWeight", 4, (0.0f, 20.0f)),
    ("noiseAmount", 8, (0.0f, 1.0f)),
    ("fxRouteType", 8, (0.0f, 1.0f))
  );


  private val colorMap:  Map[Int, Color] = Map(
    1 -> Color(182, 80, 80),
    2 -> Color(2, 10, 100),
    3 -> Color(132, 80, 100),
    4 -> Color(12, 130, 170),
    5 -> Color(12, 20, 130),
    6 -> Color(12, 180, 70),
    7 -> Color(52, 80, 70),
    8 -> Color(150, 0, 0),
    9 -> Color(0, 150, 0)
  )


  val midiDeviceName = "BCR2000, USB MIDI, BCR2000"

  var mySynth: Option[Synthesizer] = None

  var currentChannel = 0;
  var currentOctave = 1;


  val midiInput = MidiCommunication.createMidiInputByDeviceIndex(1)
  if (midiInput.isDefined) {

    midiInput.get.receipt.observe({
      x => println(x)

      x match {
        case m: MidiNoteOnMsg => noteOn(m.channel, m.note)
        case m: MidiNoteOffMsg => noteOff(m.channel, m.note)
        case _ => println("Message dropped")
      }

      true;
    })

  }

  val midiOutput = MidiCommunication.createMidiOutputByDeviceIndex(4)


  // controller id should be larger than 20 and < 40
  def sendControlMessage(controllerChannel: Int, controllerNumber: Int, controllerValue: Float) : Unit = {
    if(this.midiOutput.isDefined) {
      this.midiOutput.foreach(output => {
        output.senderAction(new MidiCtrlMsg(controllerChannel, controllerNumber, controllerValue)) //MidiCtrlMsg(channel: Int, num: Int, data: Float)
      })
    }
  }

  def noteOn(midiChan: Int, midiNote: Int) {
    if (mySynth.isDefined) {
      if (midiChan == currentChannel) {

        val frequency = ((12 * currentOctave) + (midiNote % 12) + 60).midicps // middle C + octave + offset via keyboard

        mySynth.get.parameters() = ("frequency" -> frequency)
        mySynth.get.parameters() = ("gate" -> 1.0)
      }
    }

  }

  def noteOff(midiChan: Int, midiNote: Int) {
    if (mySynth.isDefined) {
      if (midiChan == currentChannel) {
        mySynth.get.parameters() = ("gate" -> 0.0)
      }
    }
  }

  val mySynthDef = buildSynth(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1)


  mySynth = Some(mySynthDef.play())


  mySynth.get.parameters.observe({
    x => println(x); true
  })

  val xoffset = VPDSynthApp.width/10
  val yoffset = VPDSynthApp.height/2
  //val font = createFont(processing.core.PApplet pa, java.lang.String fontFileName, int fontSize, MTColor fillColor, boolean antiAliased) 
  val font = FontManager.getInstance.createFont(VPDSynthApp, "SansSerif", VPDSynthApp.width/100, new MTColor(255,255,255), new MTColor(255,255,255), true)

  
  
  def labeledSlider(position: Vector3D, name: String, min: Float, max: Float, color: Color, parent: MTComponent): MTSlider = {
    val text = TextArea()
    text.setText(name)
    text.setPickable(false)
    text.setFont(font)

    val info = TextArea()
    info.setFont(font)
    info.setPickable(false)

    val slider = Slider(min, max, VPDSynthScene.SliderLength, VPDSynthScene.SliderWidth)
    slider.getKnob.setFillColor(new MTColor(255,255,255))


    slider.rotateZGlobal(Vec3d(0,0,0), 0f)
    slider.setPositionGlobal(position)

    text.rotateZ(Vec3d(0,0,0), 0f)
    text.setPositionGlobal(Vec3d(position.x, position.y-VPDSynthApp.height/25))

    info.setPositionGlobal(Vec3d(position.x-VPDSynthApp.width/100, position.y+VPDSynthApp.height/25))


    info.text <~ slider.value.map( x => x.formatted("%2.2f"))

    text.strokeColor() = color
    slider.fillColor() = color
    slider.strokeColor() = color
    info.strokeColor() = color

    parent += info ++ slider ++ text

    slider
  }

  def upDownThing(name: String, min: Int, max: Int, value: Int, color: Color, callback: Int => Unit): MTComponent = {
    val text = TextArea()

    val bup = Button("+1")
    bup.setFont(font)    
    val bdown = Button("-1")
    bdown.setFont(font)    

    text.setPickable(false)
    text.setFont(font)    
    //bup.setPickable(false)
    //bdown.setPickable(false)

    text += bup ++ bdown

    bup.setPositionRelativeToParent(Vec3d(60, bup.getHeightXY(TransformSpace.GLOBAL)/2))
    bdown.setPositionRelativeToParent(Vec3d(-40, bdown.getHeightXY(TransformSpace.GLOBAL)/2))

    var currentValue = value

    text.text() = currentValue+""

    bup.pressed.observe {
      x => println(x)
      if (x && currentValue + 1 <= max) {
        currentValue = currentValue + 1; text.text() =  currentValue + ""
      }; callback(currentValue); true
    }
    bdown.pressed.observe {
      x => println(x)
        if (x && currentValue - 1 >= min) {
        currentValue = currentValue - 1; text.text() =  currentValue + ""
      }; callback(currentValue); true
    }

    bdown.fillColor() = color                                      //info.setPositionRelativeToParent(Vec3d(0, -10f))
    bup.fillColor() = color
    bdown.strokeColor() = color                                      //info.setPositionRelativeToParent(Vec3d(0, -10f))
    bup.strokeColor() = color


    //valLabel.strokeColor() = color

    text

  }


    val image = app.loadImage("background.png")
    image.resize(app.width, app.height)
    val backgroundImage = new MTBackgroundImage(app, image, false)
    canvas().addChild(backgroundImage)

    val bg = new MTRectangle(VPDSynthApp, 0 , 0, VPDSynthApp.width, VPDSynthApp.height);
    bg.setFillColor(Color(40f,40f,40f));
    bg.setPickable(false)
    canvas += bg

    val cuts = Array(0,4,8,12,14)
    
    val rectHeight = VPDSynthApp.height/7
    val inputWidth = VPDSynthApp.width/8
    val padding = VPDSynthApp.width/100
    
    val rect1 = new MTRoundRectangle(VPDSynthApp, VPDSynthApp.width - VPDSynthApp.width/8, VPDSynthApp.height/2, 0, VPDSynthApp.width/8, rectHeight, VPDSynthApp.width/50, VPDSynthApp.width/50)
    
    val rect2 = new MTRoundRectangle(VPDSynthApp, - VPDSynthApp.width/4 + rectHeight, VPDSynthApp.width/4, 0, VPDSynthApp.width/2, rectHeight, VPDSynthApp.width/50, VPDSynthApp.width/50)  
    
    val rect3 = new MTRoundRectangle(VPDSynthApp, VPDSynthApp.width/2 - 2*padding, padding, 0, VPDSynthApp.width/2 + padding, rectHeight, VPDSynthApp.width/50, VPDSynthApp.width/50)   
    
    val rect4 = new MTRoundRectangle(VPDSynthApp, VPDSynthApp.width/6 + padding, VPDSynthApp.height - rectHeight - padding, 0, VPDSynthApp.width/2 + padding, rectHeight, VPDSynthApp.width/50, VPDSynthApp.width/50)
    
    val rect5 = new MTRoundRectangle(VPDSynthApp, VPDSynthApp.width/6, padding, 0, VPDSynthApp.width/4, rectHeight, VPDSynthApp.width/50, VPDSynthApp.width/50)

    val rects = Array(rect1,rect2,rect3,rect4,rect5)
    
    rects.foreach(rect => {
      rect.setFillColor(new MTColor(0,0,0,50))
      rect.setStrokeColor(new MTColor(0,0,0,100))
      rect.getInputProcessors().foreach(ip => {
        if (ip.isInstanceOf[ScaleProcessor]) {
          rect.unregisterInputProcessor(ip)
				}
			})
		  rect.removeAllGestureEventListeners(classOf[ScaleProcessor])     
    })
   
    canvas += rect1 ++ rect2 ++ rect3 ++ rect4 ++ rect5
    
   val parmName = parameterMapping(0)._1
   val parmRange = parameterMapping(0)._3
   val group = parameterMapping(0)._2 
   val position = Vec3d(rect1.getCenterPointGlobal.getX, rect1.getCenterPointGlobal.getY)
   val slider1 = labeledSlider(position, parmName, parmRange._1, parmRange._2, colorMap(group), rects(0))   
    mySynth.get.parameters <~ slider1.value.map({
      x => parmName -> x
    })

    if (parmName != "noiseAmount") {
      slider1.value() = (parmRange._1 + parmRange._2) / 4.0f
    } else {
      slider1.value() = 0f
    }   
    
   
   for (i <- 1 to 4) {
      val parmName = parameterMapping(i)._1
      val parmRange = parameterMapping(i)._3
      val group = parameterMapping(i)._2    
      val position = Vec3d(rect2.getCenterPointGlobal.getX - rect2.getWidthXY(TransformSpace.GLOBAL)/2 + inputWidth/2 + (i-1)*inputWidth, rect2.getCenterPointGlobal.getY)
      val slider = labeledSlider(position, parmName, parmRange._1, parmRange._2, colorMap(group), rects(1))    
      mySynth.get.parameters <~ slider.value.map({
      x => parmName -> x
      })
      if (parmName != "noiseAmount") {
        slider.value() = (parmRange._1 + parmRange._2) / 4.0f
      } else {
        slider.value() = 0f
      }   
   }
   
   for (i <- 1 to 4) {
      val parmName = parameterMapping(i+4)._1
      val parmRange = parameterMapping(i+4)._3
      val group = parameterMapping(i+4)._2     
      val position = Vec3d(rect3.getCenterPointGlobal.getX - rect3.getWidthXY(TransformSpace.GLOBAL)/2 + inputWidth/2 + (i-1)*inputWidth, rect3.getCenterPointGlobal.getY)
      val slider = labeledSlider(position, parmName, parmRange._1, parmRange._2, colorMap(group), rects(2))           
      mySynth.get.parameters <~ slider.value.map({
        x => parmName -> x
      })
  
      if (parmName != "noiseAmount") {
        slider.value() = (parmRange._1 + parmRange._2) / 4.0f
      } else {
        slider.value() = 0f
      }         
   }
   
   for (i <- 1 to 4) {   
      val parmName = parameterMapping(i+8)._1
      val parmRange = parameterMapping(i+8)._3
      val group = parameterMapping(i+8)._2
      val position = Vec3d(rect4.getCenterPointGlobal.getX - rect4.getWidthXY(TransformSpace.GLOBAL)/2 + inputWidth/2 + (i-1)*inputWidth, rect4.getCenterPointGlobal.getY)
      val slider = labeledSlider(position, parmName, parmRange._1, parmRange._2, colorMap(group), rects(3))          
      mySynth.get.parameters <~ slider.value.map({
        x => parmName -> x
      })
  
      if (parmName != "noiseAmount") {
        slider.value() = (parmRange._1 + parmRange._2) / 4.0f
      } else {
        slider.value() = 0f
      }         
   }

   for (i <- 1 to 2) {     
      val parmName = parameterMapping(i+12)._1
      val parmRange = parameterMapping(i+12)._3
      val group = parameterMapping(i+12)._2     
      val position = Vec3d(rect5.getCenterPointGlobal.getX - rect5.getWidthXY(TransformSpace.GLOBAL)/2 + inputWidth/2 + (i-1)*inputWidth, rect5.getCenterPointGlobal.getY)
      val slider = labeledSlider(position, parmName, parmRange._1, parmRange._2, colorMap(group), rects(4))          
      mySynth.get.parameters <~ slider.value.map({
        x => parmName -> x
      })
  
      if (parmName != "noiseAmount") {
        slider.value() = (parmRange._1 + parmRange._2) / 4.0f
      } else {
        slider.value() = 0f
      }         
   }
   
    rect1.rotateZGlobal(rect1.getCenterPointGlobal, 270)   
    rect2.rotateZGlobal(rect2.getCenterPointGlobal, 90)
    rect3.rotateZGlobal(rect3.getCenterPointGlobal, 180)
    rect5.rotateZGlobal(rect5.getCenterPointGlobal, 180)      
   
    val specialRect = new MTRoundRectangle(VPDSynthApp, 3*VPDSynthApp.width/4, VPDSynthApp.height - 2*rectHeight - padding, 0, VPDSynthApp.width/6, 2*rectHeight, VPDSynthApp.width/50, VPDSynthApp.width/50)
    specialRect.setFillColor(new MTColor(0,0,0,50))
    specialRect.setStrokeColor(new MTColor(0,0,0,100))
    specialRect.getInputProcessors().foreach(ip => {
        if (ip.isInstanceOf[ScaleProcessor]) {
          specialRect.unregisterInputProcessor(ip)
				}
			})
    specialRect.removeAllGestureEventListeners(classOf[ScaleProcessor])
    
  val (cx, cy) = (specialRect.getCenterPointGlobal.getX, specialRect.getCenterPointGlobal.getY)

  val octave = upDownThing("Octave", -2, 8, 1, colorMap(8), {
    x: Int => currentOctave = x
  })
  octave.setPositionGlobal(Vec3d(cx,cy - 3*rectHeight/5))

  val pattern = upDownThing("Pattern", 0, 5, 0, colorMap(9), {
    x: Int => currentChannel = x ; println("pattern"+x)
  })
  pattern.setPositionGlobal(Vec3d(cx, cy - 1*rectHeight/5))

  val mod1 = Slider(0,1,VPDSynthScene.SliderLength, VPDSynthScene.SliderWidth)
  mod1.getKnob.setStrokeColor(new MTColor(150,150,150))
  mod1.setPositionGlobal(Vec3d(cx, cy + 1*rectHeight/5))

  mod1.value.observe {x=> this.sendControlMessage(currentChannel, 21, x) ;true}

  val mod2 = Slider(0,1,VPDSynthScene.SliderLength, VPDSynthScene.SliderWidth)
  mod2.getKnob.setStrokeColor(new MTColor(150,150,150))  
  mod2.setPositionGlobal(Vec3d(cx, cy + 3*rectHeight/5))

  mod2.value.observe {x=> this.sendControlMessage(currentChannel, 22, x) ;true}

  specialRect += octave ++ pattern ++ mod1 ++ mod2
  canvas += specialRect

}







