# How to Contribute

If you think that you can add a new feature or want to fix a bug, we invite you to contribute to FunwithPhysics and make this project better. To start contributing, follow the below instructions:

1. Create a folder at your desire location (usually at your desktop).

2. Open Git Bash Here

3. Create a Git repository.

   Run command `git init`

4. Fork this Repo

5. Clone your forked repository of project.

```git clone
git clone https://github.com/<your_username>/Sleepometer-Android-App.git
```

6. Navigate to the project directory.

```
cd Sleepometer-Android-App
```

7. Add a reference(remote) to the original repository.

```
git remote add upstream https://github.com/maityamit/Sleepometer-Android-App.git
```

8. Check the remotes for this repository.

```
git remote -v
```

9. Always take a pull from the upstream repository to your main branch to keep it updated as per the main project repository.

```
git pull upstream main
```

10. Create a new branch(prefer a branch name that relates to your assigned issue).

```
git checkout -b <YOUR_BRANCH_NAME>
```

11. Perform your desired changes to the code base.

12. Check your changes.

```
git status
```

```
git  diff
```

13. Stage your changes.

```
git add . <\files_that_you_made_changes>
```

14. Commit your changes.

```
git commit -m "relavant message"
```

15. Push the committed changes in your feature branch to your remote repository.

```
git push -u origin <your_branch_name>
```

16. To create a pull request, click on `compare and pull requests`.

17. Add an appropriate title and description to your PR explaining your changes.

18. Click on `Create pull request`.

Congratulations🎉, you have made a PR to the FunwithPhysics.
Wait for your submission to be accepted and your PR to be merged by a maintainer.
