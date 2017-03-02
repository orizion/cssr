"use strict";
const fs = require('fs');
const path = require('path');
const http = require('http');
const exec = require('child_process').exec;
const spawn = require( 'child_process' ).spawn;

// This is just a small utility that uses swager-codegen to generate typescript code
// TODO: Use swagger-codegen-cli v 2.2.2 (fixes some relevant bugs)
// TODO: Use real swagger.json

const download = function(url, dest, cb) {
  var file = fs.createWriteStream(dest);
  var request = http.get(url, function(response) {
    response.pipe(file);
    file.on('finish', function() {
      file.close(cb);  // close() is async, call cb after close completes.
    });
  }).on('error', function(err) { // Handle errors
    fs.unlink(dest); // Delete the file async. (But we don't check the result)
    if (cb) cb(err.message);
  });
};

if (!fs.existsSync("swagger-codegen-cli.jar")) {
	download("http://central.maven.org/maven2/io/swagger/swagger-codegen-cli/2.2.1/swagger-codegen-cli-2.2.1.jar", "swagger-codegen-cli.jar", 
	function() {
		generateCode();
	});
}
else {
	generateCode();
}
function generateCode() {
	var codegen = spawn( 'java', [ '-jar', 'swagger-codegen-cli.jar',
		'generate',
		'-i', 'swagger.json',
		'-c', 'swaggerconfig.json',
		'-l', 'typescript-fetch',
		'-o', 'temp'] );

	codegen.stdout.on( 'data', data => {
		console.log( `${data}` );
	});

	codegen.stderr.on( 'data', data => {
		console.error( `${data}` );
	});

	codegen.on( 'close', code => {
		console.log( `child process exited with code ${code}` );
		process.exit();
	});
}