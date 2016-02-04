# Process Killer

A simple app for testing application state saving and restoring.

<img src="art/process-killer-demo.gif" alt="Demo" width="400px">

## Contents

* [Setup](#setup)
* [Compatibility](#compatibility)
* [Description](#description)
* [License](#license)

<a name="setup"></a>
## Setup

Clone the repo

    git clone https://github.com/livefront/process-killer-android.git

and build in Android Studio.

<a name="compatibility"></a>
## Compatibility

* **Minimum SDK**: 21
* **Target SDK**: 23
* **Device Types**: Phone & Tablet
* **Devices Supported**: All
* **Orientations Supported**: Portrait and landscape
* **RTL Support**: Yes

<a name="description"></a>
## Description

The purpose of the app is to allow for testing how well apps restore their state after the system kills their host process. Applications with recent activity are displayed in a list; clicking on any of those items kills their host process (if they are currently running) in the same way the system would when reclaiming memory. The app can then be restarted from the link in the Snackbar or navigated to via a Launcher or Recents.

Note that running processes will only be killed if the system deems the action "safe". See the [documentation](http://developer.android.com/guide/topics/processes/process-lifecycle.html) for more details.

<a name="license"></a>
## License

    Copyright 2016 Livefront

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
