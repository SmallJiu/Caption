@echo off
set y=%date:~0,4%
set month=%date:~5,2%
set day=%date:~8,2%
set hour=%time:~0,2%
set min=%time:~3,2%
set sec=%time:~6,2%

set msg=%y%-%month%-%day%-%hour%-%min%-%sec%
set "msg=%msg: =%"
title %msg%

git branch %msg%
git branch
git checkout %msg%
pause

title %msg%
git add .
pause

git commit -m "commit-%msg%"
pause

echo =============
git log --pretty=oneline
echo =============
pause
