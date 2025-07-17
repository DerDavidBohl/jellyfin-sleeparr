# Jellyfin-Sleeparr

This App pauses the Playback after some Time to prevent too much progress if you fall asleep.

## Disclaimer

**!! Jellyfin-Sleeparr is still in development. The usage and API might change in the future !!**

## Setup

### Requirements

- Access Token for your Jellyfin API
- Plugin `Playback Reporting` installed (Available in Plugin Catalog)

### docker-compose
```yml
services:
  sleeparr:
    image: ghcr.io/derdavidbohl/jellyfin-sleeparr:latest
    container_name: sleeparr
    restart: unless-stopped
    volumes:
      - /path/to/your/data:/app/data
    environment:
      - SLEEPARR_JELLYFIN_ENDPOINT=<Your Jellyfin Instance endpoint i. e. http://jellyfin:8096/>
      - SLEEPARR_JELLYFIN_APIKEY=<Your Jellyfin API Key>
      - SLEEPARR_JWT_SECRET= # Your Secret. Can be generated with: openssl rand -base64 32
      - SLEEPARR_DEFAULTS_WATCHDURATION= # Optional, Duration String. Default duration for auto pause. If not set: 3h
      - SLEEPARR_DEFAULTS_DIFFERENTITEMS= # Optional, Number. Default number of different items for auto pause. If not set: 3
      - SLEEPARR_DEFAULTS_ENABLED= # Optional, Boolean. Indicates if auto pause enabled by default. If not set: true
```

## Ho it works

Sleeparr finds the Playback Reports Data matching to your Session (DeviceName, ClientName, User) and sums the Duration of the content, that was watched more than 10 seconds. 
If the Duration in the last 6 Hours is greater than the configured maximum inactivity and the user/session watched 2 or more diffrent contents, sleeparr pauses the playback with a message.

## Roadmap

- [x] Implement Basic Functionallity
- [ ] Add a UI for better configuration
- [ ] Remove Playback Reporting Plugin dependency
