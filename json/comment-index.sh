#!/bin/bash
curl --request PUT --header "Content-Type: application/json" "http://localhost:9000/comments" --data @`dirname $0`/comments.json
echo ""
