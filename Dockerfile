FROM ubuntu:latest
LABEL authors="arena"

ENTRYPOINT ["top", "-b"]