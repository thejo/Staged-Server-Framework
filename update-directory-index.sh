#!/bin/bash

# Source - http://chkal.blogspot.com/2010/09/maven-repositories-on-github.html
for DIR in $(find ./repository -type d); do
  (
    echo "<html><body><h1>Directory listing</h1><hr/><pre>"
    ls -1pa "${DIR}" | grep -v "^\./$" | grep -v "^index\.html$" | awk '{ printf "<a href=\"%s\">%s</a>",$1,$1 }'
    echo "</pre></body></html>"
  ) > "${DIR}/index.html"
done
