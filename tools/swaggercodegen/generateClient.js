"use strict";
const fs = require('fs');
const fs_extra = require('fs-extra');
const path = require('path');
const http = require('http');
const exec = require('child_process').exec;
const spawn = require( 'child_process' ).spawn;

// This is just a small utility that uses swager-codegen to generate typescript code
// the swagger-codegen-cli-custom.jar is generated from https://github.com/aersamkull/swagger-codegen

downloadFile("http://localhost:8090/v2/api-docs", "swagger.json", function(err) {
	if(err) {
		console.error(err);
	}
	else {
		generateCode(copyFile);
	}
});


function generateCode(cb) {
	var codegen = spawn( 'java', [ '-jar', 'swagger-codegen-cli-custom.jar',
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
		if(cb) {
			cb(); 
		}
	});
}
function copyFile() {
	fs_extra.copySync('temp/api.ts', '../../client/src/services/api.ts');

}

function downloadFile(url, dest, cb) {
	
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
}