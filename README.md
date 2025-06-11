# Jellyfin-Sleeparr

This App pauses the Playback after some Time to prevent too much progress if you fall asleep.

## Setup

### docker-compose
```yml
services:
  sleeparr:
    image: ghcr.io/derdavidbohl/jellyfin-sleeparr:latest
    container_name: sleeparr
    restart: unless-stopped
    networks:
      - internal
      - local-services
    depends_on:
      - jellyfin
    environment:
      - SLEEPARR_JELLYFIN_ENDPOINT=<Your Jellyfin Instance endpoint i. e. http://jellyfin:8096/>
      - SLEEPARR_JELLYFIN_APIKEY=<Your Jellyfin API Key>
      - SLEEPARR_MONITOREDUSERNAMES=<comma separated List of usernames to monitor>
      - SLEEPARR_MAXIMUMINACTIVITY=2h

```

