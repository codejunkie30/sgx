#!/bin/bash
cd /phantom/pdfcache/
find *.pdf -type f -mmin +5 -exec rm {} \;