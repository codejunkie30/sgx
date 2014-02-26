(function() {

  require({
    urlArgs: "b=" + ((new Date()).getTime()),
    paths: {
      jquery: 'vendor/jquery.ui/jquery-1.10.2',

      jquicore: 'vendor/jquery.ui/ui/jquery.ui.core',
      jquiwidget: 'vendor/jquery.ui/ui/jquery.ui.widget',
      jquimouse: 'vendor/jquery.ui/ui/jquery.ui.mouse',
      accordion: 'vendor/jquery.ui/ui/jquery.ui.accordion',
      slider: 'vendor/jquery.ui/ui/jquery.ui.slider',
      tabs: 'vendor/jquery.ui/ui/jquery.ui.tabs'
    },
    shim: {
      jquery: {
        exports: '$'
      },
      jquicore: {
        deps: ['jquery']
      },
      jquiwidget: {
        deps: ['jquery', 'jquicore']
      },
      jquimouse: {
        deps: ['jquery', 'jquicore', 'jquiwidget']
      },
      accordion: {
        deps: ['jquery', 'jquicore', 'jquiwidget', 'jquimouse']
      },
      slider: {
        deps: ['jquery', 'jquicore', 'jquiwidget', 'jquimouse']
      },
      tabs: {
        deps: ['jquery', 'jquicore', 'jquiwidget']
      }
    }
  }, ['app/SGX-main'], function($) {
    
  });

}).call(this);
