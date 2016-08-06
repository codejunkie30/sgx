"use strict";


var gulp = require('gulp');
var concat = require('gulp-concat');


var config = {
  paths: {
    dist:'./dist',
    css: [
      'node_modules/bootstrap/dist/css/bootstrap.min.css',
      'node_modules/bootstrap/dist/css/bootstrap-theme.min.css',
      'node_modules/toastr/build/toastr.css'
    ]
  }
}



gulp.task('css', function(){
  gulp.src(config.paths.css)
    .pipe(concat('vendor.css'))
    .pipe(gulp.dest(config.paths.dist+'/css'));
});


gulp.task('default', ['css']);