(ns sudoku-solver.core
  (:gen-class))

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

(defn valid? [collection]
  "validate that there are no duplicates. Filter out all 0s and then check if all distinct"
;;cannot just apply distinct as distinct takes varargs and not seq
  (apply distinct? (filter pos-int? collection))
)

(defn complete? [collection]
  "check if row is complete"
  (every? pos-int? collection)
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
  "replace the value in the list at specified position"
  [pos collection val]
  (concat (take pos collection) (list val) (nthnext collection (inc pos))))

;; ********************************* logic ***************************


(defn getrow [grid rowid]
  "Given the grid the function returns the nth row, where nth is defined by row id"
  (nth grid rowid))

(defn getcol [grid colid]
  "Given the grid the function returns the nth column, where nth is defined by colid"
  (map #(nth % colid) grid))

(defn getbox [grid rowid colid]
  "Given the grid and any cell coordinate (row and col id) the method will return the data associated with the 3x3
square that the cell belongs to. The function returns a seq back"
  ;; given the row and col of cell find the top row and col coordinates of the square
  (let [firstrow (* 3 (quot rowid 3)) firstcol (* 3 (quot colid 3)) lastrow (+ firstrow 3) lastcol (+ firstcol 3)]
    (let [rows (map #(getrow grid %) (range firstrow lastrow))]
      (apply concat (map #(sublist firstcol lastcol %) rows))
      )
    )
)

(defn getval [grid rowid colid] 
  "Given the grid and cell coordinates the function will return the value of the cell"
  (-> (getrow grid rowid) (nth colid)))

(defn check-grid [grid fn]
  "Given the grid and validation function the function will run through every row and will apply the function to it.
If function fails the result will be false."
  (every? fn 
          (concat
           (map #(getrow grid %) row_range)
           (map #(getcol grid %) row_range)
           (let [rg (range 0 9 3)]
             (apply concat (for [x rg] (map #(getbox grid x %) rg))))
           )
          ))

(defn findrow [grid]
  "Given grid, find the first row with non filled cell"
  (let [rowId
        (first (keep-indexed #(when-not (complete? (getrow grid %2)) %1) row_range))
        ]
    (if-not rowId (throw (Exception. "Cannot find non complete row. Is grid complete?")))
    rowId
    )
)

(defn find-row-and-col
  "Given grid, find the first the coordinates of the first cell that was not filled"
  ([grid] (find-row-and-col grid (findrow grid)))
  ([grid rowid] (first 
                 (keep-indexed #(when (zero? %2) (list rowid %1)) (getrow grid rowid)))
   )
)

(defn generate-new-grid
  "given th grid, cell coordinates and new value return back a new grid with new value"
  [grid row col val]
  (->> (replace-in-list col (getrow grid row) val) (replace-in-list row grid))
)


;; ************************** Runner code ***************************

(defn run
  [grid stats]

  (println "Before",grid, stats)
  (if-not (check-grid grid valid?) 
    '(nil stats)
    (if (check-grid grid complete?) 
      '(grid stats)
     
      (let [[row col] (find-row-and-col grid) new_stats stats]
        (println "After", grid, stats, new_stats)
        (loop [val 1]
          (let [new_grid (generate-new-grid grid row col val)
                result (run new_grid (inc new_stats))]
            
            (if (and (< val 9) (nil? (first result)))
              (recur (inc val))
              result)
            )
          )
        )
      )
    )
  )
      




(defn -main
  [& args]
  (println "Hello sudoku")
  (println (run grid 0))
)
