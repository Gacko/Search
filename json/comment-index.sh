#!/bin/bash
curl --request POST --header "Content-Type: application/json" "http://localhost:9000/posts/1/comments" --data @`dirname $0`/comments.json
echo ""
