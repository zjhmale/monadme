(ns monad.clojure.repo)

(defprotocol UserRepository
  (fetch-by-id! [this id])
  (create! [this user username])
  (update! [this old-user new-user username]))

(defn mk-user-repo []
  (reify UserRepository
    (fetch-by-id! [this id]
      (prn "Fetched user id " id))
    (create! [this user username]
      (prn "Create user triggered by " username))
    (update! [this old-user new-user username]
      (prn "Updated user triggered by " username))))

