#!/bin/bash
curl --request DELETE "http://localhost:9000/posts/1377495/tags/1" --silent && echo
curl --request DELETE "http://localhost:9000/posts/1377495/tags/2" --silent && echo
