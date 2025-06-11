# Jellyfin-Sleeparr

This App pauses the Playback after some Time to prevent too much progress if you fall asleep.

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
    environment:
      - SLEEPARR_JELLYFIN_ENDPOINT=<Your Jellyfin Instance endpoint i. e. http://jellyfin:8096/>
      - SLEEPARR_JELLYFIN_APIKEY=<Your Jellyfin API Key>
      - SLEEPARR_MONITOREDUSERNAMES=<comma separated List of usernames to monitor>
      - SLEEPARR_MAXIMUMINACTIVITY=2h
```

## Ho it works

Sleeparr finds the Playback Reports Data matching to your Session (DeviceName, ClientName, User) and sums the Duration of the content, that was watched more than 10 seconds. 
If the Duration in the last 6 Hours is greater than the configured maximum inactivity and the user/session watched 2 or more diffrent contents, sleeparr pauses the playback with a message.

## Roadmap

- [x] Implement Basic Functionallity
- [ ] Add a UI for better configuration
- [ ] Remove Playback Reporting Plugin dependency
