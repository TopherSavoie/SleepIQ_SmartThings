/**
 *  Sleep Number
 *
 *  Copyright 2019 Topher Savoie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
preferences {
	input(name: "foundation", type: "bool", title: "Adjustable Foundation", required: "true", defaultValue: false)
}
metadata {
	definition (name: "Sleep IQ", namespace: "TopherSavoie", author: "TopherSavoie", cstHandler: true) {
		capability "Switch Level"
        capability "Switch"
        capability "PresenceSensor"
        
        attribute "bedId", "String"
        attribute "side", "String"
        
        
        command "setBedId", ["string"]
        command "setSide", ["string"]
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale:2){	
        	standardTile("Switch", "device.switch", width: 4, height: 4, canChangeIcon: false) {
              state "on", label:'RAISED', action:"off", icon:"st.Bedroom.bedroom2", backgroundColor:"#79b821"
              state "off", label:'FLAT', action:"on", icon:"st.Bedroom.bedroom2", backgroundColor:"#ffffff"
        	}
        
        	controlTile("LevelSliderControl", "device.level", "slider", height: 4, width: 2, range:"(0..100)") {
   			  state "level", action:"switch level.setLevel"
		    }
            
		    valueTile("Side", "device.side", width: 2, height: 1){
        	  state "default", label: '${currentValue} Side'
       		}
            standardTile("Presence", "device.presence", width: 4, height: 4, canChangeBackground: true)
		    {
              state "present", label: "In Bed", labelIcon:"st.presence.tile.present", backgroundColor:"#00a0dc"
              state "not present", label: "Not in Bed", labelIcon:"st.presence.tile.not-present", backgroundColor:"#ffffff"
            }            
            
		    //valueTile("Presence", "device.PresenceState", width: 4, height: 4){
        	//  state "default", label: '${currentValue}'
        	//} 

            valueTile("Level", "device.level", width: 4, height: 1, inactiveLabel: false, decoration: "flat") {
			  state "level", label: 'Sleep Number: ${currentValue}'
		    } 
		    
            main("Presence")
		    details("Side", "Level", "Presence", "Switch", "LevelSliderControl")
    }
}

def updateData(String state, Integer sleepNumber, boolean present){
	sendEvent(name: "switch", value: state)
	sendEvent(name: "level", value: sleepNumber)
    if(present)
		sendEvent(name: "presence", value: "present")
    else
		sendEvent(name: "presence", value: "not present")
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'level' attribute

}

def setLevel(level) {
	sendEvent(name: "level", value: level)
    String side = "L"
    if(device.latestState('side').stringValue == "Right"){
    	side = "R"
    }
    parent.setNumber(device.latestState('bedId').stringValue, side, Math.round(level))
}


def setBedId(val){
	sendEvent(name: "bedId", value: val)
}

def setSide(val){
	sendEvent(name: "side", value: val)
}

def on(){
    String side = "L"
    if(device.latestState('side').stringValue == "Right"){
    	side = "R"
    }
	sendEvent(name: "switch", value: "on")
    parent.raiseBed(device.latestState('bedId').stringValue, side)
}

def off(){
    String side = "L"
    if(device.latestState('side').stringValue == "Right"){
    	side = "R"
    }
	sendEvent(name: "switch", value: "off")
    parent.lowerBed(device.latestState('bedId').stringValue, side)
}

