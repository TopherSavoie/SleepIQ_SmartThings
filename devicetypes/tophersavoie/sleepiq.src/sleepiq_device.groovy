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
        command "levelUp" 
        command "levelDown" 
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale:2){	
            //Used for the main page only, the detal is the PRIMARY_CONTROL attribute and I didnt want that.
            standardTile("Presence", "device.presence", width: 4, height: 4, canChangeBackground: true)
		    {
              state "present", label: "In Bed", backgroundColor:"#00a0dc"
              state "not present", label: "Not in Bed", backgroundColor:"#ffffff"
            }  
            
            //Using thermostat type in order to use the OPERATING_STATE attribute
            multiAttributeTile(name:"MultiTile", type:"thermostat", width:6, height:4) {
                tileAttribute("device.level", key: "PRIMARY_CONTROL") {
                    attributeState("level", label:'${currentValue}', defaultState: true, backgroundColors:[
                        [value: 0, color: "#ff0000"],
                        [value: 20, color: "#ffff00"],
                        [value: 40, color: "#00ff00"],
                        [value: 60, color: "#00ffff"],
                        [value: 80, color: "#0000ff"],
                        [value: 100, color: "#ff00ff"]
                    ])
                }
                tileAttribute("device.level", key: "VALUE_CONTROL") {
                    attributeState("VALUE_UP", action: "levelUp")
                    attributeState("VALUE_DOWN", action: "levelDown")
                }
                
                tileAttribute("device.side", key: "SECONDARY_CONTROL") {
                    attributeState("default", label: '${currentValue} Side')
                }                

                tileAttribute("device.presence", key: "OPERATING_STATE") {
                  attributeState("present", label: "In Bed", backgroundColor:"#00a0dc")
                  attributeState("not present", label: "Not in Bed", backgroundColor:"#cccccc")
                }        

            }               
            
            
            main("Presence")
            details("MultiTile")

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

def levelUp(){
    int newSetpoint = device.currentValue("level")
    log.info "newSetpoint '${newSetpoint}'"
	setLevel(newSetpoint+5)
}

def levelDown(){
    int newSetpoint = device.currentValue("level")
    log.info "newSetpoint '${newSetpoint}'"    
	setLevel(newSetpoint-5)
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

