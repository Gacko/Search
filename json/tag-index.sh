#!/bin/bash
curl --request PUT --header "Content-Type: application/json" "http://localhost:9000/tags" --data @`dirname $0`/tags.json
echo ""
