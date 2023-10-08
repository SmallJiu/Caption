@echo off
set y=%date:~0,4%
set month=%date:~5,2%
set day=%date:~8,2%
set hour=%time:~0,2%
set min=%time:~3,2%
set sec=%time:~6,2%

set msg=%y%.%month%.%day%-%hour%.%min%.%sec%
set "msg=%msg: =%"
title %msg%

git add .
git commit -m "%msg%"

echo =============
git log --pretty=oneline
echo =============
exit