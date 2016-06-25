#!/bin/bash
curl --request POST --header "Content-Type: application/json" "http://localhost:9000/posts" --data @`dirname $0`/post.json
echo ""
