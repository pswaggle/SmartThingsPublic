/**
 *  Plex Integration
 *
 *  Copyright 2015 Christian Hjelseth
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "PlexIntegration",
    namespace: "ChristianH",
    author: "Christian Hjelseth",
    description: "Allows web requests to dim/turn off/on lights when plex is playing.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: [displayName: "iMPServer", displayLink: ""])

preferences {
	section("Control these bulbs...") {
		input "hues", "capability.colorControl", title: "Which Hue Bulbs?", required:false, multiple:true
	}
    section ("..and these switches..") {
        input "switches", "capability.switch", multiple: true, required: false
    }
    section("Configuration") {
        input(name: "bSwitchOffOnPause", type: "bool", title: "Turn switches off on pause")
        input(name: "iLevelOnStop", type: "number", title: "Bulb levels on Stop", defaultValue:100)
        input(name: "iLevelOnPause", type: "number", title: "Bulb levels on Pause", defaultValue:30)
        input(name: "iLevelOnPlay", type: "number", title: "Bulb levels on Play", defaultValue:0)
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
}

mappings {
  path("/dostuff/:command") {
    action: [
      GET: "OnCommandRecieved"
    ]
  }
}

def OnCommandRecieved() {
	def command = params.command
	log.debug "OnCommandRecieved: $command"
    
    if (command == "onplay") {
	SetHuesLevel(iLevelOnPlay)
        SetSwitchesOff()
    }
    else if (command == "onpause") {
    	SetHuesLevel(iLevelOnPause)
        if( bSwitchOffOnPause == "true") {
       		SetSwitchesOff()
        } else {
        	SetSwitchesOn()
        }
    }
    else if (command == "onstop") {
    	SetHuesLevel(iLevelOnStop)
        SetSwitchesOn()
    }
}

def SetSwitchesOn() {
	switches?.on()
}
def SetSwitchesOff() {
	switches?.off()
}
def SetHuesLevel(level) {
	log.debug "SetHuesLevel: $level"
	hues*.setLevel(level)
}