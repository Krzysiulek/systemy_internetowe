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

let Model = function (path) {
    let self = this;
    let getPath = function (query) {
        let parentId = self.isGrade ? viewController.currentStudent : null;
        return API_URL + self.path.split('/').join('/' + parentId + '/') + (!query ? '/' : query);
    };

    this.path = path;
    this.isGrade = (path === 'students/grades');
    this.objects = ko.observableArray();
    this.idFieldName = (path === 'students') ? 'index' : 'id';

    self.get = function (query) {
        console.log('GET');
        return $.ajax({
            url: getPath(query),
            type: "GET",
            accept: 'application/json',
            contentType: 'application/json'
        });
    };

    self.post = function (object) {
        console.log('POST');
        return $.ajax({
            url: getPath(),
            type: "POST",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    self.put = function (object) {
        return $.ajax({
            url: getPath() + object[self.idFieldName](),
            type: "PUT",
            data: ko.mapping.toJSON(object),
            accept: 'application/json',
            contentType: 'application/json'
        })
    };

    self.delete = function (object) {
        console.log('DELETE');
        return $.ajax({
            url: getPath() + object[self.idFieldName](),
            type: "DELETE",
        })
    };

};

var getAllData = function (viewController) {
    viewController.get(viewController.students);
    viewController.get(viewController.courses);
};

let ViewController = function () {
    var self = this;
    self.students = new Model('students');
    self.courses = new Model('courses');
    self.grades = new Model('students/grades');

    self.newStudent = Student();
    self.newGrade = Grade();
    self.newCourse = Course();

    self.currentStudent = 0;

    self.addObject = function (objectName, group) {
        group.post(self[objectName])
            .then(() => {
                setTimeout(() => {
                    window.location.reload();
                }, 1);
            })
    };

    var dataToObservable = function (data) {
        var observables = ko.observableArray();
        data.forEach(function (object) {
            var newObject = {};
            for (var field in object) {
                if (field !== 'link' && object.hasOwnProperty(field)) {
                    newObject[field] = ko.observable(object[field]);
                }
            }
            observables.push(newObject);
        });
        return observables;
    };

    self.get = function (group, query) {
        group.get(query).then(function (data) {
            var observables = dataToObservable(data);
            group.objects.removeAll();
            observables().forEach(function (o) {
                group.objects.push(o);
            });
            observables().forEach(function (object) {
                for (var field in object) {
                    if (object.hasOwnProperty(field) && typeof object[field] === "function") {
                        object[field].subscribe(function (changes) {
                            group.put(object);
                        }, null, 'change')
                    }
                }
            });
        });
    };

    self.delete = function (object, group) {
        group.delete(object).then(function () {
            group.objects.remove(object);
        });
    };

    self.onGoToGrades = function (index) {
        window.location.assign(window.location.href.replace("students", "grades"));

        self.currentStudent = index;
        self.get(self.grades);
        return true;
    };

    self.getQuery = function (field) {
        var query = '?';
        for (var f in field) {
            if (field.hasOwnProperty(f) && field[f]()) {
                query += f + '=' + field[f]() + '&';
            }
        }
        return query;
    };

    self.filters = {
        students: {
            index: ko.observable(),
            name: ko.observable(),
            surname: ko.observable(),
            birthday: ko.observable()
        },
        courses: {
            id: ko.observable(),
            name: ko.observable(),
            lecturer: ko.observable()
        },
        grades: {
            date: ko.observable(),
            minValue: ko.observable(),
            course: ko.observable()
        }
    };
    for (const categoryName in self.filters) {
        if (self.filters.hasOwnProperty(categoryName)) {
            const category = self.filters[categoryName];
            for (var field in category) {
                if (category.hasOwnProperty(field)) {
                    category[field].subscribe(function () {
                        self.get(self[categoryName], self.getQuery(category));
                    }, null, 'change')
                }
            }
        }
    }
};


var viewController = new ViewController();
getAllData(viewController);

$(function () {
    setTimeout(function () {
        ko.applyBindings(viewController);
    }, 10);
});
