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

## Command Line Options
``` 
java -jar mediaArchiver.jar source destination
``` 

## Program Flow
Still tinkering, but here is the overall process

1. In the source directory, there will be a database file of some sort. If it is not present, create it in an empty state. This file stores:
  1. A quick SHA1 of each file (first 64kb?) to enable quick detection of new files, renames, moves, etc
  2. A full SHA1 of each file
  3. Cached results from EXIFTool
2. Scan the source directory looking for new files, add them to the database as needed.
3. In the destination directory, there will be a configuration file that stores how to encode media targeted for that destination. Load this file.
4. In the destination directory, there will be a status file with the full SHA1 of every file that has been archived to this destination, in addition to the relative path to the archived file. Load this file.
5. Loop over every new file from ``` source ```, encoding it with the configuration from step 3, to ``` destination ```. Update the status file in ``` destination ``` as needed. 