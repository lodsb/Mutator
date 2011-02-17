/***********************************************************************
 * mt4j Copyright (c) 2008 - 2009 Christopher Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.input.inputData;

import org.mt4j.components.IMTTargetable;
import org.mt4j.components.interfaces.IMTComponent;
import org.mt4j.components.interfaces.IMTComponent3D;
import org.mt4j.input.MTEvent;

/**
 * The Class MTInputEvent. The base class for all input events.
 *
 * @author Christopher Ruff
 */
public class MTComponent3DInputEvent extends MTInputEvent<IMTComponent3D> {

    /**
     * Instantiates a new mT input event.
     *
     * @param source the source
     */
    public MTComponent3DInputEvent(Object source) {
        this(source, null);
    }

    public MTComponent3DInputEvent(Object source, IMTComponent3D target) {
        this(source, target, true);//FIXME?
    }

    /**
     * Instantiates a new mT input event.
     *
     * @param source the source
     * @param target the target component
     */
    public MTComponent3DInputEvent(Object source, IMTComponent3D target, boolean bubbles) {
        super(source, target);
        this.propatationStopped = false;
        this.bubbles = bubbles;
        this.eventPhase = CAPTURING_PHASE; //FIXME?
    }


    /**
     * Gets the target of this input event.
     * <br><strong>NOTE:</strong> Not every event has a target component! To check this
     * we can call <code>event.hasTarget()</code>.
     *
     * @return the target component
     * @see #getTarget()
     * @deprecated use getTarget() instead
     */
    public IMTComponent3D getTargetComponent() {
        return (IMTComponent3D) target;
    }


    /**
     * This method is invoked right before the event is fired.
     * This can be used to do event specific actions if needed before firing.
     * <br>NOTE: this is called internally and should not be called by users!
     */
    public void onFired() {
    }


    private short eventPhase;
    public static final short CAPTURING_PHASE = 1; // The current event phase is the capturing phase.
    public static final short AT_TARGET = 2;         //  The event is currently being evaluated at the target EventTarget.
    public static final short BUBBLING_PHASE = 3;//  The current event phase is the bubbling phase.


    private boolean propatationStopped;
    private boolean bubbles;

    /**
     * The <code>setEventPhase</code> method is used by the DOM implementation
     * to change the value of a <code>eventPhase</code> attribute on the
     * <code>Event</code> interface.
     *
     * @param phase Specifies the <code>eventPahse</code> attribute on the
     *              <code>Event</code> interface.
     */
    public void setEventPhase(short phase) {
        this.eventPhase = phase;
    }

    public short getEventPhase() {
        return this.eventPhase; //TODO set in constructor?
    }


    public void stopPropagation() {
        this.propatationStopped = true;
    }

    public boolean isPropagationStopped() {
        return propatationStopped;
    }

    public boolean getBubbles() {
        return bubbles;
    }

}
