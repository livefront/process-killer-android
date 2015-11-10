# Process Killer

## Contents

* [Setup](#setup)
* [Compatibility](#compatibility)
* [Description](#description)

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

The purpose of the app is to allow for easily testing how well apps restore their state after the system kills their host process when reclaiming memory. Applications with recent activity are displayed in a list; clicking on any of those items kills their host process (if they are currently running) in the same way the system would. If the app contained an entry in Recents, navigating back to the killed app via the Recents entry forces the app to be reconstructed from the app's saved state.
