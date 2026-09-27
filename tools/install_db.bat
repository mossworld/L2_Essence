@echo off
setlocal enabledelayedexpansion
:: UTF-8 코드페이지 설정 (한글 깨짐 방지)
chcp 65001 > nul

REM ###################### CONFIGURAÇÃO ######################
REM ## Altere aqui os parâmetros do banco de dados conforme necessário
set "mysqlBinPath=C:\Program Files\MariaDB 11.8\bin"

set "dbuser=root"
set "dbpass=0402"
set "dbname=samuraii"
set "dbhost=localhost"
set "sqlFolder=sql"
REM ###########################################################

set "mysqldumpPath=%mysqlBinPath%\mysqldump.exe"
set "mysqlPath=%mysqlBinPath%\mysql.exe"

REM Verifica se o MySQL está instalado corretamente
if not exist "%mysqlPath%" (
    echo.
    echo 오류: "%mysqlPath%" 경로에서 mysql.exe를 찾을 수 없습니다.
    echo MariaDB/MySQL 설치 경로가 올바른지 확인해주세요.
    pause
    exit /b
)

echo.
echo =====================================================
echo            데이터베이스 설치 프로그램 (DATABASE INSTALLER)
echo =====================================================
echo.
echo 설치 유형을 선택하세요 / Choose installation type:
echo.
echo [F] 전체 설치 (기존 데이터가 모두 "삭제"됩니다)
echo     Full Installation (will delete everything)
echo.
echo [S] 기본 데이터만 설치 (민감한 사용자 데이터 제외)
echo     Skip character data (only static server tables)
echo.
echo [Q] 종료 / Quit
echo.

:askinstall
set "choice=x"
set /p "choice=옵션을 입력하세요 / Enter choice (F/S/Q): "
if /i "!choice!"=="f" goto confirmFull
if /i "!choice!"=="s" goto install
if /i "!choice!"=="q" goto end
goto askinstall

:confirmFull
set "confirm=x"
set /p "confirm=정말로 모든 데이터를 삭제하고 초기화하시겠습니까? (Y/N): "
if /i "!confirm!"=="y" goto fullinstall
if /i "!confirm!"=="n" goto askinstall
goto confirmFull

:fullinstall
echo.
echo 기존 데이터베이스를 삭제하고 재생성하는 중...
"%mysqlPath%" -h %dbhost% -u %dbuser% --password=%dbpass% -e "DROP DATABASE IF EXISTS %dbname%; CREATE DATABASE %dbname%;"
echo.

:install
echo.
echo "%sqlFolder%" 폴더에서 SQL 파일 설치를 시작합니다...
echo.

for %%f in ("%sqlFolder%\*.sql") do (
    echo [설치 중] %%~nxf...
    "%mysqlPath%" -h %dbhost% -u %dbuser% --password=%dbpass% %dbname% < "%%f"
)

echo.
echo =====================================================
echo 데이터베이스 설치가 완료되었습니다. (Installation complete)
echo =====================================================
pause
goto end

:end
exit /b