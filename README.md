# README #

This project is programmed in Java and depends on LibGDX and the LWJGL3 backend.

### Skin Composer ###

* Use Skin Composer to create skins for scene2d.ui
* Version 11

### Changes ###

*Fixed unable to hide welcome screen setting
*Enforce using "_data" folder when "Keep resources relative" is selected. Solves #20
*Fixed alignment property of the TextField preview having no effect.
*Fixed renaming a style selects a different style afterwards
*Fixed wrong warnings when exporting imported skins
*Updated to LibGDX 1.9.6

### Contact ###

* This project is maintained by Raymond "Raeleus" Buckley
* http://ray3k.wordpress.com
* raymond.ray3k (at) gmail.com

### Notes ###

To run Skin Composer on OSX, download the .jar version of the app and run it via terminal with the following command: **java -jar -XstartOnFirstThread skin_composer.jar**

Contributer RaimundWege has shared the following information for anyone who is working from source in Android Studio on Mac.

* You have to go to 'File/Project Structure...' and set the project language level to '8 - Lambdas, type annotations etc.'
* The line: 'config.setWindowSizeLimits(675, 400, -1, -1);' in DesktopLauncher throws NoSuchMethodError (no idea why). So this line needs to be commented out.
* You have to start the project with the vm option: -XstartOnFirstThread

These will remain as issues until I can get my hands a proper Mac to do testing. AFAIK, the compiled version should work fine on Mac systems.

### License ###
MIT License

Copyright (c) 2017 Raymond Buckley

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
