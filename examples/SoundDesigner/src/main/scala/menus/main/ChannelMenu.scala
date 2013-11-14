package ui.menus.main

import org.mt4j.Application
import org.mt4j.components.MTComponent
import org.mt4j.components.visibleComponents.shapes.MTRectangle
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle
import org.mt4j.components.TransformSpace

import org.mt4j.util.math.Vector3D
import org.mt4j.util.math.Vertex
import org.mt4j.types.{Vec3d}
import org.mt4j.components.visibleComponents.widgets.MTSlider

import org.mt4j.util.MTColor

import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor 
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor 
import org.mt4j.input.inputProcessors.IGestureEventListener
import org.mt4j.input.inputProcessors.MTGestureEvent
import org.mt4j.input.gestureAction.DefaultDragAction

import org.mt4j.util.animation.Animation
import org.mt4j.util.animation.AnimationEvent
import org.mt4j.util.animation.IAnimationListener
import org.mt4j.util.animation.MultiPurposeInterpolator

import processing.core.PGraphics

import scala.actors._

import ui.menus._
import ui._
import ui.util._
import ui.audio._
import ui.input._


object ChannelMenu {
  
  final val Width = 200
  final val Height = 200
  final val Color = new MTColor(0,20,80,40)
  
  private var registry = Set[ChannelMenu]()
  
  def +=(menu: ChannelMenu) = {   
    this.registry += menu
  }
  
  def -=(menu: ChannelMenu) = {
    this.registry -= menu
  }
  
  def isMenuVisible(channelNumber: Int) = {
    this.registry.exists(_.channelNumber == channelNumber)
  }  
  
  def menuFromChannelNumber(channelNumber: Int) : Option[ChannelMenu] = {
    this.registry.find(_.channelNumber == channelNumber)
  }
  
  def apply(app: Application, center: Vector3D, channelNumber: Int) = {
      new ChannelMenu(app, center, channelNumber: Int)
  }  
  
}


class ChannelMenu(app: Application, center: Vector3D, val channelNumber: Int) 
      extends MTRoundRectangle(app, center.getX - ChannelMenu.Width/2f, center.getY - ChannelMenu.Height/2f, 0, ChannelMenu.Width, ChannelMenu.Height, 10, 10) {
        
    this.setFillColor(ChannelMenu.Color)
    this.setNoStroke(true)
    this.setupInteraction()
    
    private def setupInteraction() = {
      //remove defaults
      this.unregisterAllInputProcessors() //no default rotate, scale & drag processors
      this.removeAllGestureEventListeners() //no default listeners as well
                    
      //register input processors
      this.registerInputProcessor(new DragProcessor(app))
      val tapProcessor = new TapProcessor(app)
      tapProcessor.setEnableDoubleTap(true)
      this.registerInputProcessor(tapProcessor)
      
      //add gesture listeners
      this.addGestureListener(classOf[DragProcessor], new BoundedDragAction(0, 0, Ui.width, Ui.height)) 
      this.addGestureListener(classOf[TapProcessor], new ChannelMenuTapListener(this))
      
      val slider1 = new MTSlider(app, center.getX - ChannelMenu.Width/2f, center.getY - ChannelMenu.Height/2f, ChannelMenu.Width/2f, 20, 0f, 1f)
      this.addChild(slider1)
    }    

    
    override def drawComponent(g: PGraphics) = {
      super.drawComponent(g)
      this.drawSymbol(g)
    }  
  
    def drawSymbol(graphics: PGraphics) = {
      val center = this.getCenterPointLocal()
      val cx = center.getX()
      val cy = center.getY()  
      
      graphics.noStroke()
      val (r,g,b,a) = (ChannelMenu.Color.getR, ChannelMenu.Color.getG, ChannelMenu.Color.getB, ChannelMenu.Color.getAlpha)
      graphics.fill(r, g, b, (a+50)%255)
  
      if (channelNumber == 0) {
        graphics.ellipse(cx, cy, 20, 20)
      }
      else {
        (1 to channelNumber + 1).foreach(item => {
          val (x,y) = Functions.positionOnCircle(cx, cy, 0.5f * 50, 2*math.Pi.toFloat, item, channelNumber + 1)
          graphics.ellipse(x, y, 20, 20)
        })
      }    
      
    }     
    
  
}