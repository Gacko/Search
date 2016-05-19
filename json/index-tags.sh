#!/bin/bash
curl --request POST --header "Content-Type: application/json" "http://localhost:9000/tags/bulk" --data @`dirname $0`/tags.json
echo ""
