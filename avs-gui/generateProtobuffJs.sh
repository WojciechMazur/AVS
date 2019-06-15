#!/bin/bash

protoFiles=$(ls ../protobuf/*.proto)
OutFile="./src/protobuff/protobuff.js"

echo "${protoFiles}"
npx pbjs -t static-module -w es6 ${protoFiles[@]} -o $OutFile
npx pbts -o ./src/protobuff/protobuff.d.ts ${OutFile}