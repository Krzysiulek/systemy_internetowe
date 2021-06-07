'use strict';
const API_URL = 'http://localhost:8000/';

let Student = () => {
    return {
        index: ko.observable(),
        firstName: ko.observable(),
        lastName: ko.observable(),
        birthday: ko.observable()
    }
};

let Grade = () => {
    return {
        id: null,
        value: ko.observable(),
        course: ko.observable(),
        date: ko.observable()
    }
};

let Course = () => {
    return {
        id: null,
        lecturer: ko.observable(),
        name: ko.observable()
    }
};

let isGrade = (path) => (path === 'students/grades');

let idFieldName = (path) => (path === 'students') ? 'index' : 'id';

let convertToObservable = (data) => {
    let observables = ko.observableArray();
    data.forEach((object) => {
        let newObject = {};

        for (let field in object) {
            if (field !== 'link' && object.hasOwnProperty(field)) {
                newObject[field] = ko.observable(object[field]);
            }
        }

        observables.push(newObject);
    });
    return observables;
};

let Model = function (path) {
    this.path = path;
    this.objects = ko.observableArray();

    let getPath = (query) => {
        let parentId = isGrade(this.path) ? viewController.currentStudent : null;
        return API_URL + this.path.split('/').join('/' + parentId + '/') + (!query ? '/' : query);
    };

    this.get = (query) => {
        return $.ajax({
            url: getPath(query),
            type: "GET",
            accept: 'application/json',
            contentType: 'application/json'
        });
    };

    this.post = (object) => {
        return $.ajax({
            url: getPath(),
            type: "POST",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    this.put = (object) => {
        console.log(ko.mapping.toJSON(object));
        return $.ajax({
            url: getPath() + object[idFieldName(this.path)](),
            type: "PUT",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    this.delete = (object) => {
        return $.ajax({
            url: getPath() + object[idFieldName(this.path)](),
            type: "DELETE",
        })
    };

};

let updateStudentsAndCourses = () => {
    viewController.get(viewController.students);
    viewController.get(viewController.courses);
};

let ViewController = function () {
    this.students = new Model('students');
    this.newStudent = Student();
    this.currentStudent = 0;

    this.courses = new Model('courses');
    this.newCourse = Course();
    this.selectedCourseId = 0;

    this.grades = new Model('students/grades');
    this.newGrade = Grade();


    this.addObject = function (objectName, group) {
        if (objectName === 'newGrade') {
            this.newGrade.course = {
                id: this.selectedCourseId
            }
        }

        group.post(this[objectName])
            .then(() => {
                setTimeout(() => {
                    window.location.reload();
                }, 1);
            })
    };

    this.get = (group, query) => {
        group.get(query).then((data) => {
            let observableData = convertToObservable(data);
            group.objects.removeAll();
            observableData().forEach(o => group.objects.push(o));

            observableData().forEach((object) => {
                for (let field in object) {
                    if (object.hasOwnProperty(field) && typeof object[field] === "function") {
                        object[field].subscribe(() => group.put(object));
                    }
                }
            });
        });
    };

    this.delete = (object, group) => group.delete(object).then(() => group.objects.remove(object));

    this.goToGrades = (index) => {
        window.location.assign(window.location.href.replace("students", "grades"));
        this.currentStudent = index;
        this.get(this.grades);
    };
};


const viewController = new ViewController();
updateStudentsAndCourses();
$(() => setTimeout(() => ko.applyBindings(viewController), 1));
