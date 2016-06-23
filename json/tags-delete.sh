#!/bin/bash
curl --request DELETE "http://localhost:9000/posts/1/tags/1"
curl --request DELETE "http://localhost:9000/posts/1/tags/2"
echo ""
