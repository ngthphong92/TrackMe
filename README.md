
# Project TrackMe

## User Stories

The following **REQUIRED** functionality is completed:

* [x] The application is required to support Android 5.0+
* [x] User should be noticed by the application to grant all permissions: Location Permission, Location Service, etc.
* [x] List out all workout sessions. User may have unlimited sessions in history, latest session is on the top of list
* [x] Application is required to work smoothly while user scrolls down to view sessions in past
* [x] Each session: Display Map Route, Distance, Duration and Average Speed
* [x] User taps on Record button to start a new session
* [x] Record Current Speed, Current Distance and Duration
* [x] Display Current Speed, Current Distance and Duration
* [x] Start Location should be displayed on the Map
* [x] Current Location of user should be displayed on the Map
* [x] Route should be displayed on the Map
* [x] User should be able to Pause, Resume, Stop current session
* [x] To save battery, the application is required to be able run in background or when phone screen is off
* [x] When the application is in background and there is active session is being recorded, if user opens the app again, it should navigate user to active session (Record screen):
      ➢ Current Distance, Current Speed and Duration are required to be updated correctly when user opens the app from background
      ➢ Route Map is required to be updated correctly when user opens the app from background
      ➢ Pause, Resume and Stop button should keep its state when user opens the app from background

The following **STUFF** tech are used:

* [x] Room
* [x] Pager3
* [x] ViewModel
* [x] Koin DI
* [x] Navigation
* [x] DataBinding
* [x] MVVM
* [x] Material design

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='https://github.com/ngthphong92/Assets/blob/main/walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

## Open-source libraries used

- [Easy-Permission](https://github.com/googlesamples/easypermissions) - EasyPermissions is a wrapper library to simplify basic system permissions logic when targeting Android M or higher.

## License

    Copyright 2021 Nguyen Thanh Phong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.