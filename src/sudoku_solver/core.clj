(ns sudoku-solver.core
  (:gen-class))

;;(def grid (map #(concat (range % 10)(range 1 %))(range 1 10)))
(def grid '(
           (5 3 0 0 7 0 0 0 0)
           (6 0 0 1 9 5 0 0 0)
           (0 9 8 0 0 0 0 6 0)
           (8 0 0 0 6 0 0 0 3)
           (4 0 0 8 0 3 0 0 1)
           (7 0 0 0 2 0 0 0 6)
           (0 6 0 0 0 0 2 8 0)
           (0 0 0 4 1 9 0 0 5)
           (0 0 0 0 8 0 0 7 9)
))

(def row_range (range 0 9))

;; ********************************* utils ***************************

(defn valid? [vec]
;; validate that there are no duplicates
;; filter out all 0s and then check if all distinct
;; cannot just apply distinct as distinct takes varargs and not seq
  (apply distinct? (filter pos-int? vec))
)

(defn complete? [vec]
;; check if all complete
  (every? pos-int? vec)
)

(defn in?
  "true if collection contains elm"
  [elm collection]
  (some #(= elm %) collection)
)

(defn sublist
  "returns sub list of collection"
  [start end collection]
  (let [idxrg (range start end)]
    (keep-indexed #(if (in? %1 idxrg) %2) collection)
  )
)

(defn replace-in-list
  [pos collection val]
  (concat (take pos collection) (list val) (nthnext collection (inc pos))))

;; ********************************* logic ***************************


(defn getrow [grid rowid] 
  (nth grid rowid))

(defn getcol [grid colid] 
  (map #(nth % colid) grid))

(defn getbox [grid rowid colid]
  ;; given the row and col of cell find the top row and col coordinates of the square
  (let [firstrow (* 3 (quot rowid 3)) firstcol (* 3 (quot colid 3)) lastrow (+ firstrow 3) lastcol (+ firstcol 3)]
    (let [rows (map #(getrow grid %) (range firstrow lastrow))]
      (apply concat (map #(sublist firstcol lastcol %) rows))
      )
    )
)


(defn check-grid [grid fn]
  (every? fn 
          (concat
           (map #(getrow grid %) row_range)
           (map #(getcol grid %) row_range)
           (let [rg (range 0 9 3)]
             (apply concat (for [x rg] (map #(getbox grid x %) rg))))
           )
          ))

(defn findrow [grid]
  ;; given grid - find the first row with non filled cell
  (let [rowId
        (first (keep-indexed #(when-not (complete? (getrow grid %2)) %1) row_range))
        ]
    (if-not rowId (throw (Exception. "Cannot find non complete row. Is grid complete?")))
    rowId
    )
)

(defn find-row-and-col
  ([grid] (find-row-and-col grid (findrow grid)))
  ([grid rowid] (first 
                 (keep-indexed #(when (zero? %2) (list rowid %1)) (getrow grid rowid)))
   )
)

(defn generate-new-grid
  [grid row col val]
  (->> (replace-in-list col (getrow grid row) val) (replace-in-list row grid))
)

(defn run_inc [grid fn]
  "The function is responsible for looping through every value from 1..9, validating that 
the grid is valid and calling the run method recursively again with new grid. if run method returns null
than this method reverts to previous grid and tries next value for that position"
  (let [[row col] (find-row-and-col grid)]
    (loop [val 1]
      (let [new-grid (generate-new-grid grid row col val) temp (fn new-grid)]
        (if (and (< val 9) (nil? temp))
          (recur (inc val))
            temp
          )
        )
      )
    )
)
      


(defn run [grid]
  (if (check-grid grid valid?)
    (if (check-grid grid complete?) grid (run_inc grid run))
    nil
    )
)

(defn -main
  [& args]
  (println "Hello sudoku")
  (println (run grid))
)
