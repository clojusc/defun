(defproject clojusc/defun "0.3.0"
  :description "A Clojure macro supporting functions with pattern matching heads a la LFE"
  :url "https://github.com/clojusc/defun"
  :license {
    :name "Eclipse Public License"
    :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [org.clojure/core.match "0.3.0-alpha4"]
    [org.clojure/tools.macro "0.1.2"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :test {
      :plugins [
        [lein-ltest "0.3.0"]]}}
  :aliases {
    "ubercompile" ["with-profile" "+ubercompile" "compile"]
    "test" ["with-profile" "+test" "ltest"]})
