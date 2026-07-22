@echo off
cd /d "%~dp0server"
if not exist ".env" (
  echo.
  echo  No .env found. Copying from .env.example...
  echo  Edit server\.env with your values before starting.
  echo.
  copy .env.example .env > nul
)
if not exist "data" mkdir data > nul
npm install
npm start
