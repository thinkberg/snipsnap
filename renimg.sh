#! /bin/sh
for i in *-image-*; do
  new=image-`echo $i | sed 's/-image-/-/'`
  echo renaming $i to $new
  mv "$i" "$new"
done
