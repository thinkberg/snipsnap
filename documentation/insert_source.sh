cp SnipSnap_Developer.tex SnipSnap_Developer.tex.bak
ruby cut.rb SnipSnap_Developer.tex  > tmp.tex && mv tmp.tex SnipSnap_Developer.tex
