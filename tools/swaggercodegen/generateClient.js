"use strict";
const fs = require('fs');
const fs_extra = require('fs-extra');
const path = require('path');
const http = require('http');
const exec = require('child_process').exec;
const spawn = require( 'child_process' ).spawn;

// This is just a small utility that uses swager-codegen to generate typescript code
// the swagger-codegen-cli-custom.jar is generated from https://github.com/aersamkull/swagger-codegen
// TODO: Use real swagger.json

generateCode(copyFile);

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