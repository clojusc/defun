(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m =>\u001B[m "))

(defproject clojusc/defun "0.4.0"
  :description "A Clojure macro supporting functions with pattern matching heads a la LFE"
  :url "https://github.com/clojusc/defun"
  :license {
    :name "Eclipse Public License"
    :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.10.0"]
    [org.clojure/core.match "0.3.0"]
    [org.clojure/tools.macro "0.1.5"]]
  :profiles {
    :dev {
      :source-paths ["dev-resources/src"]
      :repl-options {
        :init-ns defun.repl
        :prompt ~get-prompt}}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.3.5"]
        [lein-ancient "0.6.15"]
        [lein-kibit "0.1.6"]]}
    :ubercompile {
      :aot :all}
    :test {
      :plugins [
        [lein-ltest "0.3.0"]]}}
  :aliases {
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "lint" ["with-profile" "+lint" "kibit"]
    "ubercompile" ["with-profile" "+ubercompile" "compile"]
    "test" ["with-profile" "+test" "ltest"]})
