GOPROJECT := certmgr
GOCMDS := $(GOPROJECT)
GOMODULE := github.com/hdecarne-github/$(GOPROJECT)
GOMODULE_VERSION :=  $(shell cat version.txt)

GO := $(shell command -v go 2> /dev/null)
ifdef GO
GOOS ?= $(shell go env GOOS)
GOARCH ?= $(shell go env GOARCH)
LDFLAGS := $(LDFLAGS) -X $(GOMODULE)/internal/buildinfo.version=$(GOMODULE_VERSION) -X $(GOMODULE)/internal/buildinfo.timestamp=$(shell date +%Y%m%d%H%M%S)
ifneq (windows, $(GOOS))
GOCMDEXT :=
else
GOCMDEXT := .exe
endif
endif

NPM := $(shell command -v npm 2> /dev/null)
NPMOPTS ?= --no-progress --no-color

WEB ?= 1

.DEFAULT_GOAL := help

.PHONY: help
help:
	@echo "Please use 'make <target>' where <target> is one of the following:"
	@echo "  make deps\tprepare dependencies"
	@echo "  make build\tbuild artifacts"
	@echo "  make dist\tcreate release package"
	@echo "  make test\ttest artifacts"
	@echo "  make clean\tdiscard build artifacts (not dependencies)"

.PHONY: init
init:
	@echo "Using build environment:"
ifndef GO
    $(error "ERROR: go command is not available")
endif
	@echo "  GO: $(GO) (GOOS:$(GOOS), GOARCH:$(GOARCH))"
ifndef NPM
    $(error "ERROR: npm command is not available")
endif
	@echo "  NPM: $(NPM)"

.PHONY: deps
deps: deps-web deps-go

.PHONY: deps-init
deps-init: init
	@echo "Preparing dependencies..."

.PHONY: deps-web
deps-web: deps-init
ifeq (1, $(WEB))
	cd web && $(NPM) $(NPMOPTS) install
endif

.PHONY: deps-go
deps-go: deps-init
	$(GO) mod download -x

.PHONY: build
build: build-go

.PHONY: build-init
build-init: deps
	@echo "Building artifacts..."

.PHONY: build-web
build-web: build-init
ifeq (1, $(WEB))
	cd web && $(NPM) $(NPMOPTS) run build
endif

.PHONY: build-go
build-go: build-web
	mkdir -p "build/bin"
	$(foreach GOCMD, $(GOCMDS), $(GO) build -ldflags "$(LDFLAGS)" -o "./build/bin/$(GOCMD)$(GOCMDEXT)" ./cmd/$(GOCMD);)

.PHONY: dist
dist: dist-build

.PHONY: dist-init
dist-init: build
	@echo "Creating release package..."

.PHONY: dist-build
dist-build: dist-init
	mkdir -p build/dist
	tar czvf build/dist/$(GOPROJECT)-$(GOOS)-$(GOARCH)-$(GOMODULE_VERSION).tar.gz -C build/bin .

.PHONY: test
test: test-web test-go

.PHONY: test-init
init-init: deps
	@echo "Testing artifacts..."

.PHONY: test-web
test-web: test-init
ifeq (1, $(WEB))
	cd web && $(NPM) $(NPMOPTS) run test
endif

.PHONY: test-go
test-go: test-init
	$(GO) test -ldflags "$(LDFLAGS)" -v -coverpkg=./... -covermode=atomic -coverprofile=build/coverage.out ./...

.PHONY: clean
clean: clean-build

.PHONY: clean-init
clean-init: init
	@echo "Cleaning build artifacts..."

.PHONY: clean-go
clean-go: clean-init
	$(GO) clean ./...

.PHONY: clean-build
clean-build: clean-go
	rm -rf "build"

.PHONY: tidy
tidy: init
	go mod verify
	go mod tidy
