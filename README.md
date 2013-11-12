# MediaArchiver
Over the years, I have amassed hundreds of gigabytes of family pictures and video.
Though I have an on-site backup and a cloud backup, I would like one extra layer
of backups. I want a backup that is smart enough to know about the content being
backed up, and can re-compress the media to make the backup size smaller. I don't
need iPhone video (21mbps mp4) or D90 video (40mpbs mjpeg), when transcoded MP4 at
a much lower bitrate would suffice. I don't need to back up 10MB raw files
from my D300, when in the event of a disaster, I would be fine with 100kb JPGs. 

## Design Goals

1. Encoder options can be configured
per media source - namely, the EXIF metadata. Different options can be specified
for video from the iPhone, from a DSLR, a GoPro, etc.

2. Options for the video track, audio track, and still images are stored separately