# Admin Tool SGX

Running this is pretty simple, but very different from a regular setup. 
There's a build step in between that manages dependencies and even minifying
content for production later on. Thankfully it's all handeled by webpack.

#### Requirements are
1. Node (npm comes with it)
2. Webpack (npm install -g webpack) 

not 100% if webpack needs to be installed
globally. Just try without it, it'll let you
know it's needed if it is.


#### Steps to run the project
cd inside the admin-tool directory. [I prefer gitbash on windows but windows command line works just as fine]
Run this command   `npm install`   (assuming you have node already)
This'll install all the dependencies for the project automatically

Test it
Type this in the terminal and hit enter `webpack`

Did assets compile? good. If not look at the error message and try to resolve it

After everything is set this is the command you want to run when developing 
(remember, you have to be in the project root on terminal/command prompt/gitbash etc)
`webpack --watch`

This will compile your assets every time you make a change to the code, 
So after this point it's the same as normal development. 

NOTE: your server needs to be pointed to dist/ folder inside the repo. Not the main root folder.

After you've made all your changes and are ready to ship the code, run this command.
`npm run deploy`

All your assests will be compiled and minified and you'll have a bunch of files in the dist folder.
Throw them in the s3 bucket and your'e done.