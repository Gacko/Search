#!/bin/bash
curl --request POST --header "Content-Type: application/json" "http://localhost:9000/tags" --data @`dirname $0`/tag.json
echo ""
