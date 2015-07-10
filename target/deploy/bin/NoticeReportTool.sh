#!/bin/sh
# ----------------------------------------------------------------------------
#  Copyright 2001-2006 The Apache Software Foundation.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
# ----------------------------------------------------------------------------
#
#   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
#   reserved.


# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

PRGDIR=`dirname "$PRG"`
BASEDIR=`cd "$PRGDIR/.." >/dev/null; pwd`



# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_VERSION" ] ; then
             JAVA_VERSION="CurrentJDK"
           else
             echo "Using Java version: $JAVA_VERSION"
           fi
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
           fi
           ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# If a specific java binary isn't specified search for the standard 'java' binary
if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java`
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." 1>&2
  echo "  We cannot execute $JAVACMD" 1>&2
  exit 1
fi

if [ -z "$REPO" ]
then
  REPO="$BASEDIR"/lib
fi

CLASSPATH=$CLASSPATH_PREFIX:"$BASEDIR"/./properties:"$REPO"/commons-collections-3.2.1.jar:"$REPO"/commons-lang-2.4.jar:"$REPO"/httpclient-4.0.1.jar:"$REPO"/httpcore-4.0.1.jar:"$REPO"/commons-codec-1.4.jar:"$REPO"/commons-logging-1.1.1.jar:"$REPO"/commons-cli-1.3.jar:"$REPO"/common-framework-1.4.8.jar:"$REPO"/slf4j-api-1.7.5.jar:"$REPO"/xstream-1.4.1.jar:"$REPO"/xmlpull-1.1.3.1.jar:"$REPO"/xpp3_min-1.1.4c.jar:"$REPO"/snakeyaml-1.13.jar:"$REPO"/gson-1.7.1.jar:"$REPO"/eproperties-1.1.0-r56m.jar:"$REPO"/poi-3.11-beta3.jar:"$REPO"/poi-ooxml-3.11-beta3.jar:"$REPO"/poi-ooxml-schemas-3.11-beta3.jar:"$REPO"/xmlbeans-2.6.0.jar:"$REPO"/stax-api-1.0.1.jar:"$REPO"/tika-core-1.7.jar:"$REPO"/commons-digester-2.1.jar:"$REPO"/commons-beanutils-1.8.3.jar:"$REPO"/mail-1.3.2.jar:"$REPO"/activation-1.0.2.jar:"$REPO"/velocity-tools-2.0.jar:"$REPO"/commons-chain-1.1.jar:"$REPO"/commons-validator-1.3.1.jar:"$REPO"/dom4j-1.1.jar:"$REPO"/oro-2.0.8.jar:"$REPO"/sslext-1.2-0.jar:"$REPO"/struts-core-1.3.8.jar:"$REPO"/antlr-2.7.2.jar:"$REPO"/struts-taglib-1.3.8.jar:"$REPO"/struts-tiles-1.3.8.jar:"$REPO"/velocity-1.6.2.jar:"$REPO"/xmlsec-1.3.0.jar:"$REPO"/cxf-tools-common-2.7.14.jar:"$REPO"/wsdl4j-1.6.3.jar:"$REPO"/jaxb-xjc-2.2.6.jar:"$REPO"/jaxb-impl-2.2.6.jar:"$REPO"/cxf-api-2.7.14.jar:"$REPO"/woodstox-core-asl-4.4.1.jar:"$REPO"/stax2-api-3.1.4.jar:"$REPO"/xmlschema-core-2.1.0.jar:"$REPO"/geronimo-javamail_1.4_spec-1.7.1.jar:"$REPO"/cxf-rt-core-2.7.14.jar:"$REPO"/postgresql-9.1-901-1.jdbc4.jar:"$REPO"/cf-6x-connector-1.0.7.jar:"$REPO"/protex-plugin-integration-1.0.2.jar:"$REPO"/htmlparser-1.4.jar:"$REPO"/protex-sdk-client-6.4.2.jar:"$REPO"/cxf-rt-frontend-jaxws-2.7.6.jar:"$REPO"/xml-resolver-1.2.jar:"$REPO"/asm-3.3.1.jar:"$REPO"/cxf-rt-bindings-soap-2.7.6.jar:"$REPO"/cxf-rt-databinding-jaxb-2.7.6.jar:"$REPO"/cxf-rt-bindings-xml-2.7.6.jar:"$REPO"/cxf-rt-frontend-simple-2.7.6.jar:"$REPO"/cxf-rt-ws-addr-2.7.6.jar:"$REPO"/cxf-rt-ws-policy-2.7.6.jar:"$REPO"/neethi-3.0.2.jar:"$REPO"/cxf-rt-transports-http-2.7.6.jar:"$REPO"/cxf-rt-ws-security-2.7.6.jar:"$REPO"/ehcache-core-2.5.1.jar:"$REPO"/wss4j-1.6.11.jar:"$REPO"/xmlsec-1.5.5.jar:"$REPO"/opensaml-2.5.1-1.jar:"$REPO"/openws-1.4.2-1.jar:"$REPO"/xmltooling-1.3.2-1.jar:"$REPO"/joda-time-1.6.2.jar:"$REPO"/protex-sdk-utilities-6.4.2.jar:"$REPO"/codecenter-sdk-client-6.6.0.jar:"$REPO"/codecenter-sdk-utilities-6.6.0.jar:"$REPO"/blackduck-cxf-utilities-1.1.jar:"$REPO"/jsoup-1.7.3.jar:"$REPO"/log4j-1.2.14.jar:"$REPO"/slf4j-log4j12-1.7.5.jar:"$REPO"/commons-io-2.0.jar:"$REPO"/rendersnake-1.7.jar:"$REPO"/commons-lang3-3.1.jar:"$REPO"/NoticeReportTool-1.0.1-SNAPSHOT.jar

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  [ -n "$HOME" ] && HOME=`cygpath --path --windows "$HOME"`
  [ -n "$BASEDIR" ] && BASEDIR=`cygpath --path --windows "$BASEDIR"`
  [ -n "$REPO" ] && REPO=`cygpath --path --windows "$REPO"`
fi

exec "$JAVACMD" $JAVA_OPTS -Xmx1024m -Xms1024M \
  -classpath "$CLASSPATH" \
  -Dapp.name="NoticeReportTool" \
  -Dapp.pid="$$" \
  -Dapp.repo="$REPO" \
  -Dapp.home="$BASEDIR" \
  -Dbasedir="$BASEDIR" \
  com.blackducksoftware.soleng.nrt.NoticeReportTool \
  "$@"
