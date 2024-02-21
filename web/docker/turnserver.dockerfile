FROM coturn/coturn

USER root

COPY turnserver.conf /etc/coturn/turnserver.conf 

ARG TURN_USERNAME
ARG TURN_PASSWORD
ARG TURN_DOMAIN

RUN echo "user=$TURN_USERNAME:$TURN_PASSWORD" >> /etc/coturn/turnserver.conf
RUN echo "real=$TURN_DOMAIN" >> /etc/coturn/turnserver.conf
