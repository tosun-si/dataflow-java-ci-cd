FROM gcr.io/dataflow-templates-base/java11-template-launcher-base

ARG WORKDIR=/dataflow/template
RUN mkdir -p ${WORKDIR}
WORKDIR ${WORKDIR}

COPY pom.xml .
ADD src /dataflow/template/src

ENV FLEX_TEMPLATE_JAVA_MAIN_CLASS="fr.groupbees.application.TeamLeagueApp"
ENV FLEX_TEMPLATE_JAVA_CLASSPATH="${WORKDIR}/src/main/java/fr/groupbees/application/TeamLeagueApp.java"

ENTRYPOINT ["/opt/google/dataflow/java_template_launcher"]