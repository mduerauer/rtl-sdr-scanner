#!/bin/bash

APP_VERSION=1.0.0
APP_NAME=rtl-sdr-scanner
APP_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

for i in "$@"
do
case $i in
    -e=*|--executable=*)
    RTLPWRFFT_EXEC="${i#*=}"
    shift # past argument=value
    ;;
    --fmin=*)
    ARG_FMIN="${i#*=}"
    shift # past argument=value
    ;;
    --fmax=*)
    ARG_FMAX="${i#*=}"
    shift # past argument=value
    ;;
    --demo)
    ARG_DEMO=YES
    shift # past argument with no value
    ;;
    *)
          # unknown option
    ;;
esac
done

INVALID_ARGS=N

if [ -z  ${ARG_FMIN+x} ]; then
    echo "ERROR: Please provide a minimum frequency"
    INVALID_ARGS=Y
fi

if [ -z  ${ARG_FMAX+x} ]; then
    echo "ERROR: Please provide a maximum frequency"
    INVALID_ARGS=Y
fi

JAVA_EXEC=`which java`
if [ ! -x "${JAVA_EXEC}" ]; then
    echo "ERROR: Java executable not found"
    INVALID_ARGS=Y
fi

if [ -z  ${RTLPWRFFT_EXEC+x} ]; then
    RTLPWRFFT_EXEC=`which rtl_power_fftw`
fi

if [ -x ${RTLPWRFFT_EXEC} ]; then
    echo "ERROR: Can't find rtl_power_fftw executable. If you want to launch rtl-sdr-scanner in demo mode add cli param --demo"
    INVALID_ARGS=Y
fi

if [ "${INVALID_ARGS}" == "Y" ]; then
    echo "ERROR: Invalid configuration"
    exit 1
fi

WORK_DIR=`pwd`
cd ${APP_DIR}

echo "DEBUG: min frequency:   ${ARG_FMIN} Hz"
echo "DEBUG: max frequency:   ${ARG_FMAX} Hz"
echo "DEBUG: java executable: ${JAVA_EXEC}"
echo "DEBUG: app directory:   ${APP_DIR}"
echo "DEBUG: rtl-power-fft:   ${RTLPWRFFT_EXEC}"

JAR_FILE="${APP_DIR}/build/libs/${APP_NAME}-${APP_VERSION}.jar"

if [ ! -f ${JAR_FILE} ]; then
    echo "INFO: We have to build the application."
    cd ${APP_DIR}
    ${APP_DIR}/gradlew build
fi

if [ ! -f ${JAR_FILE} ]; then
    echo "ERROR: Build failed. Please check for errors in the output."
    exit 3
fi

WRAPPER=${APP_DIR}/.rtlpwrfft-wrapper
cat > ${WRAPPER} <<EOL
#!/bin/bash
exec ${RTLPWRFFT_EXEC} -f ${ARG_FMIN}:${ARG_FMAX} -c
EOL

chmod +x ${APP_DIR}/.rtlpwrfft-wrapper

#echo "exec ${RTLPWRFFT_EXEC}" >> ${WRAPPER}

APP_CMD="${JAVA_EXEC} -jar ${JAR_FILE} ${APP_DIR}/.rtlpwrfft-wrapper"

echo "INFO: Executing ${APP_CMD}"
exec ${APP_CMD}

if [ "${WORK_DIR}" != "{$APP_DIR}" ]; then
    cd ${WORK_DIR}
fi