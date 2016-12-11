#!/bin/bash
curl --request POST --header "Content-Type: application/json" "http://localhost:9000/posts/1377495/comments" --data @`dirname $0`/comment.json --silent && echo
