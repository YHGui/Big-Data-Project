// - get command line arguments
var argv = require('minimist')(process.argv.slice(2));
var redis_host = argv['redis_host']
var redis_port = argv['redis_port']
var subscribe_channel = argv['channel']
 
// - setup dependency instance
var express = require('express');
var app = express()
var server = require('http').createServer(app)

var redis = require('redis')
console.log('creating redis client')
var redisclient = redis.createClient(redis_port, redis_host)
console.log('Subscribe to redis topic %s', subscribe_channel)
redisclient.subscribe(subscribe_channel)
redisclient.on('message', function (channel, message) {
	if (channel == subscribe_channel) {
		console.log('message reecived %s', message);
	}
})
// - setup webapp routing

app.use(express.static(__dirname = 'public'));
app.use('/jquery', express.static(__dirname = '/node_modules/jquery/dist/'));

server.listen(8080, function() {
	console.log('server started at 8080')
})
// - setup shutdown hook

