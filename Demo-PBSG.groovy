// ---------------------------------------------------------------------------------
// P B S G 2
//
// Copyright (C) 2023-Present Wesley M. Conner
//
// LICENSE
// Licensed under the Apache License, Version 2.0 (aka Apache-2.0, the
// "License"), see http://www.apache.org/licenses/LICENSE-2.0. You may
// not use this file except in compliance with the License. Unless
// required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied.
// ---------------------------------------------------------------------------------
// For reference:
//   Unicode 2190 ← LEFTWARDS ARROW
//   Unicode 2192 → RIGHTWARDS ARROW
import com.hubitat.hub.domain.Event as Event

// The Groovy Linter generates NglParseError on Hubitat #include !!!
#include wesmc.lUtils
#include wesmc.lPBSG

definition (
  namespace: 'wesmc',
  author: 'Wesley M. Conner',
  description: 'Demonstrate PushButtonSwitchGroup (PBSG) Functionality',
  singleInstance: true,
  iconUrl: '',
  iconX2Url: ''
)

preferences {
  page(name: 'Demo-PBSG')
}

void pbsg_ButtonOnCallback(Map pbsg) {
  logInfo('pbsg_ButtonOnCallback(...)', "Received button: ${pbsg.activeButton}")
}

// GUI

Map TestPBSG() {
  return dynamicPage(
    name: 'TestPBSG',
    title: [
      h1("TestPBSG - ${app.id}"),
      bullet1('Click <b>Done</b> to enable subscriptions.')
    ].join('<br/>'),
    install: true,
    uninstall: true
  ) {
    //---------------------------------------------------------------------------------
    // Per https://community.hubitat.com/t/issues-with-deselection-of-settings/36054/42:
    //   app.removeSetting('..')
    //   atomicState.remove('..')
    //---------------------------------------------------------------------------------
    app.updateLabel('TestPBSG')
    atomicState.remove('childVsws')
    section {
      atomicState.logLevel = logThreshToLogLevel('INFO')  // ERROR, WARN, INFO, DEBUG, TRACE
      //solicitLogThreshold('appLogThresh', 'INFO')  // ERROR, WARN, INFO, DEBUG, TRACE
      //atomicState.logLevel = logThreshToLogLevel(settings.appLogThresh) ?: 5
      // NOTE: atomicState.pbsgs are ALWAYS rebuilt from settings and child VSW discovery.
      // Create two PBSGs by Soliciting input data from a human
      for (i in [0, 1]) {
        Map config = config_SolicitInstance(i)
        if (config && config.name && config.allButtons) {
          // The PBSG is created and initialized as the Config is adjusted.
          // Normally, PBSG configs will be provided as a Map by the
          // application - i.e., NOT require user input via settings.
          Map pbsg = pbsg_BuildToConfig(config, 'testPBSG')
          paragraph "${pbsg_State(pbsg)}"
        } else {
          paragraph "PBSG creation is pending sufficient config data"
        }
      }
      // Create a third PBSG by hard-coding a configuration
      atomicState.TestPBSG = [
        'name': 'TestPBSG',
        'instType': 'pbsg',
        'allButtons': ['one', 'two', 'three', 'four', 'five', 'six'],
        'defaultButton': 'four'
      ]
      Map bruteForcePBSG = pbsg_BuildToConfig('TestPBSG')
      paragraph([
        h1('Debug'),
        //*appStateAsBullets(),
        //*appSettingsAsBullets(),
      ].join('<br/>'))
    }
  }
}

void initialize() {
}

void installed() {
  unsubscribe()
  initialize()
}

void uninstalled() {
}

void updated() {
  unsubscribe()
  initialize()
}