FROM phusion/baseimage:0.9.17
MAINTAINER Excellent Person <fill@me.in>

RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get install -y software-properties-common
RUN apt-get update

# Auto-accept the Oracle JDK license
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

RUN apt-get install -y oracle-java8-installer


RUN mkdir /etc/service/onyx_dash

ADD onyx-dashboard.jar /srv/onyx-dashboard.jar
ADD peer-config.edn /srv/peer-config.edn

ADD run_dashboard.sh /etc/service/onyx_dash/run
RUN chmod +x /etc/service/onyx_dash/run

EXPOSE 3000/tcp

CMD ["/sbin/my_init"]