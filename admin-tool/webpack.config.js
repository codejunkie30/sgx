
var path = require('path');
var webpack = require('webpack');
var HTMLWebpackPlugin = require('html-webpack-plugin');
var ExtractTextPlugin = require('extract-text-webpack-plugin');


var config = {
	entry: path.join(__dirname, 'src/js/main.js'),
	output: {
		path: path.resolve(__dirname, 'dist'),
		filename: 'js/bundle.js'
	},
	module: {
		loaders: [
			{
			   test: /\.html$/,
			   loader: "raw-loader"
			},
      {
          test: /\.scss$/,
          loader: ExtractTextPlugin.extract("style-loader", "css-loader!sass-loader")
      },
			// {
			// 	test: /\.scss$/,
			// 	loaders: ["style", "css", "sass"]
			// },
			{ 
				test: /\.jpe?g$|\.gif$|\.png$/, 
				//loader: require.resolve("file-loader") + "?name=../[path][name].[ext]"
				loader: "file?name=../../dist/[path][name].[ext]"
			},
			{
				test: /\.js$/,
				exclude: /(node_modules)/,
				loader: 'babel?presets[]=es2015',
			}
		]
	},
	plugins: [
	/*   */
		new ExtractTextPlugin("css/[name].css"),
		new webpack.IgnorePlugin(/^\.\/locale$/, [/moment$/]),
		new webpack.ProvidePlugin({
			$:'jquery',
			jQuery: 'jquery',
			'Promise': 'exports?global.Promise!es6-promise',
    		'fetch': 'exports?global.fetch!whatwg-fetch'
		}),

		
		new HTMLWebpackPlugin(
		{
			template: './src/index.html',
			title: 'SGX Admin', 
			inject: 'body', 
			hash:true
		}),
		new HTMLWebpackPlugin({
			template: './src/users.html',
			filename: 'users.html',
			title: 'Admin Panel',
			inject: 'body',
			hash:true
		}),
		new HTMLWebpackPlugin({
			template: './src/general.html',
			filename: 'general.html',
			title: 'Admin Panel',
			inject: 'body',
			hash:true
		}),
	]


};

module.exports = config;