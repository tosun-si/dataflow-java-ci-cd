FROM maven:3.8.6-openjdk-11-slim AS build
ADD . /app
WORKDIR /app
USER root
RUN mvn clean package -Dmaven.test.skip=true

FROM gcr.io/dataflow-templates-base/java11-template-launcher-base

ARG WORKDIR=/dataflow/template
RUN mkdir -p ${WORKDIR}
WORKDIR ${WORKDIR}

COPY --from=build /app/target/teams-league-0.1.0.jar ${WORKDIR}/target/

ENV FLEX_TEMPLATE_JAVA_MAIN_CLASS="fr.groupbees.application.TeamLeagueApp"
ENV FLEX_TEMPLATE_JAVA_CLASSPATH="${WORKDIR}/target/teams-league-0.1.0.jar"

ENTRYPOINT ["/opt/google/dataflow/java_template_launcher"]